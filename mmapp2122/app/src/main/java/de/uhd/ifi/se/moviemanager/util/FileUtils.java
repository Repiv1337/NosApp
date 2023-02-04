package de.uhd.ifi.se.moviemanager.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class FileUtils {

    // private constructor to prevent instantiation
    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    public static List<String> readAllLines(File file) throws IOException {
        try (FileReader fileReader = new FileReader(
                file); BufferedReader reader = new BufferedReader(fileReader)) {
            List<String> result = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            return result;
        }
    }

    public static void appendLine(File file, String line) throws IOException {
        appendLines(file, singletonList(line));
    }

    public static void appendLines(File file, List<String> lines)
            throws IOException {
        if (!file.exists()) {
            createDirectory(file);
        }

        try (FileOutputStream fos = new FileOutputStream(file,
                true); OutputStreamWriter osw = new OutputStreamWriter(fos,
                UTF_8); BufferedWriter writer = new BufferedWriter(osw)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        }
    }

    public static void writeLines(File file, List<String> lines)
            throws IOException {
        if (file.exists() && !wasDeletedSuccessfully(file)) {
            throw new IOException("File couldn't be deleted!");
        }
        createDirectory(file);

        try (FileWriter fileWriter = new FileWriter(
                file); BufferedWriter writer = new BufferedWriter(fileWriter)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        }
    }

    private static boolean wasDeletedSuccessfully(File file)
            throws IOException {
        return Files.deleteIfExists(file.toPath());
    }

    public static File resolve(File file, String other) {
        return new File(file, other);
    }

    public static File resolve(File file, File other) {
        return new File(file, other.getPath());
    }

    public static void createDirectory(File file) throws IOException {
        File parentDirectory = file.getAbsoluteFile();
        if (file.getName().contains(".")) {
            parentDirectory = file.getAbsoluteFile().getParentFile();
        }

        if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
            throw new IOException("Couldn't create directory '" + file
                    .getAbsolutePath() + "'");
        }
    }

    public static void delete(File file) throws IOException {
        if (file.isFile() && !wasDeletedSuccessfully(file)) {
            throw new IOException(
                    "Couldn't delete file '" + file.getAbsolutePath() + "'");
        } else if (file.isDirectory()) {
            list(file).forEach(contentFile -> {
                try {
                    delete(contentFile);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });

            if (!wasDeletedSuccessfully(file)) {
                throw new IOException("Couldn't delete directory '" + file
                        .getAbsolutePath() + "'");
            }
        }
    }

    public static File relativize(File file, File other) {
        String[] leftPath = file.getAbsolutePath()
                .split(Pattern.quote(File.separator));
        String[] rightPath = other.getAbsolutePath()
                .split(Pattern.quote(File.separator));

        int length = Math.min(leftPath.length, rightPath.length);
        int sharedElements = 0;

        for (; sharedElements < length; ++sharedElements) {
            if (!leftPath[sharedElements].equals(rightPath[sharedElements])) {
                break;
            }
        }
        if (sharedElements != length) {
            throw new IllegalArgumentException("Files have different roots!");
        }

        List<String> result;
        if (leftPath.length < rightPath.length) {
            result = Arrays.asList(rightPath)
                    .subList(sharedElements, rightPath.length);
        } else {
            result = Arrays.asList(leftPath)
                    .subList(sharedElements, leftPath.length);
        }

        return new File(StringUtils.join(result, File.separator));
    }

    public static void copy(File source, File destination) throws IOException {
        copy(source, destination, false);
    }

    private static void copy(File source, File destination,
                             boolean replaceExisting) throws IOException {
        sourceChecks(source);
        destinationChecks(destination, replaceExisting);
        if (source.isDirectory()) {
            copyEachFile(source, destination, replaceExisting);
            return;
        }

        if (destination.exists()) {
            FileUtils.delete(destination);
        } else {
            createDirectory(destination);
            if (!destination.createNewFile()) {
                throw new IOException("Creation of destination '" + destination
                        .getAbsolutePath() + "' failed!");
            }
        }

        int bufferSize = calculateBufferSize(source.length());

        try (FileOutputStream fos = new FileOutputStream(
                destination); FileInputStream fis = new FileInputStream(
                source)) {
            transferFromTo(fis, fos, bufferSize);
        }
    }

    private static void sourceChecks(File source) throws IOException {
        if (!source.exists()) {
            throw new IOException(
                    "Source '" + source.getAbsolutePath() + "' doesn't exist!");
        }
    }

    private static void destinationChecks(File destination,
                                          boolean replaceExisting)
            throws IOException {
        String absolutePath = destination.getAbsolutePath();
        if (destination.exists() && !replaceExisting) {
            throw new IOException(
                    "Destination '" + absolutePath + "' does exists and " +
                            "replace existing is not allowed!");
        } else if (destination.exists() && destination
                .isDirectory() && replaceExisting && destination
                .list().length > 0) {
            throw new IOException(
                    "Destination '" + absolutePath + "' can't be replaced, " + "because it is a non-empty directory!");
        }
    }

    private static void copyEachFile(File source, File destination,
                                     boolean replaceExisting) {
        ofNullable(source.listFiles()).map(Arrays::asList)
                .orElse(new ArrayList<>()).forEach(sourceFile -> {
            try {
                copy(sourceFile, resolve(destination, sourceFile.getName()),
                        replaceExisting);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private static int calculateBufferSize(long fileLength) {
        int minSize = 1024;
        int maxSize = 1048576;
        return Math.max(minSize, Math.min((int) (fileLength / 10), maxSize));
    }

    private static void transferFromTo(InputStream input, OutputStream out,
                                       int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        for (int read; (read = input.read(buffer)) != -1; ) {
            out.write(buffer, 0, read);
        }
    }

    public static boolean exists(File file) {
        return file.exists();
    }

    public static File get(String arg, String... args) {
        List<String> elements = new ArrayList<>();
        elements.add(requireNonNull(arg));
        for (String element : args) {
            elements.add(requireNonNull(element));
        }

        return new File(StringUtils.join(File.separator, elements));
    }

    public static Stream<File> walk(File directory) {
        List<File> queue = new ArrayList<>();
        orderedWalk(queue, directory);
        return queue.stream();
    }

    private static void orderedWalk(List<File> files, File root) {
        Deque<File> toWalk = new ArrayDeque<>();
        toWalk.push(root);

        while (!toWalk.isEmpty()) {
            File file = toWalk.pop();

            if (!file.exists()) {
                continue;
            }
            files.add(file);

            if (file.isDirectory()) {
                File[] content = ofNullable(file.listFiles())
                        .orElse(new File[]{});

                stream(content).sorted(comparing(File::isDirectory)
                        .thenComparing(File::getName)).forEach(toWalk::push);

            }
        }
    }

    public static Stream<File> list(File root) {
        return ofNullable(root.listFiles()).map(Arrays::asList)
                .orElseGet(Collections::emptyList).stream()
                .sorted(comparing(File::isDirectory)
                        .thenComparing(File::getName));
    }
}

