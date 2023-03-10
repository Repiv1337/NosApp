package de.uhd.ifi.se.moviemanager.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static de.uhd.ifi.se.moviemanager.util.FileUtils.resolve;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class FileUtilsTest {
    private static final String TEST_TEXT_NAME = "../app/src/test/res" +
            "/FileUtils_testText.txt";
    private static final String TEMPORARY = "tmp_" + System
            .currentTimeMillis() + ".txt";
    private static final String MEDIUM_SIZED_FILE = "../app/src/test/res" +
            "/entire_bee_movie_script.txt";
    private static final String TEMPORARY_DIRECTORY = "tmp-dir_" + System
            .currentTimeMillis();

    private static final List<String> TEST_TEXT_LINES = Arrays
            .asList("Hello darkness, my old friend",
                    "I've come to talk with you again",
                    "Because a vision softly creeping",
                    "Left its seeds while I was sleeping",
                    "And the vision that was planted in my brain",
                    "Still remains", "Within the sound of silence", "",
                    "In restless dreams I walked alone",
                    "Narrow streets of cobblestone",
                    "'Neath the halo of a street lamp",
                    "I turned my collar to the cold and damp",
                    "When my eyes were stabbed by the flash of a neon light",
                    "That split the night", "And touched the sound of silence",
                    "", "And in the naked light I saw",
                    "Ten thousand people, maybe more",
                    "People talking without speaking",
                    "People hearing without listening",
                    "People writing songs that voices never share",
                    "And no one dared", "Disturb the sound of silence", "",
                    "\"Fools\", said I, \"You do not know",
                    "Silence like a cancer grows",
                    "Hear my words that I might teach you",
                    "Take my arms that I might reach you\"",
                    "But my words, like silent raindrops fell", "And echoed",
                    "In the wells of silence", "",
                    "And the people bowed and prayed",
                    "To the neon god they made",
                    "And the sign flashed out its warning",
                    "In the words that it was forming",
                    "And the sign said, \"The words of the prophets are " +
                            "written on the subway walls",
                    "And tenement halls\"",
                    "And whispered in the sounds of silence");

    private static final long TEST_TEXT_SIZE = TEST_TEXT_LINES.stream()
            .map(String::length).mapToLong(i -> i + 1).sum();

    @AfterEach
    void tearDown() {
        File file = new File(TEMPORARY);
        if (file.exists()) {
            assertTrue(file.delete());
        }

        File directory = new File(TEMPORARY_DIRECTORY);
        if (directory.exists()) {
            assertDoesNotThrow(() -> FileUtils.delete(directory));
        }
    }

    @Test
    void testReadAllLines() throws IOException {
        File file = new File(TEST_TEXT_NAME);
        // precondition
        assertTrue(file.exists());

        // test
        List<String> lines = FileUtils.readAllLines(file);
        assertEquals(TEST_TEXT_LINES, lines);

        // postcondition
        assertTrue(file.exists());
    }

    @Test
    void testReadAllLinesIfFileDoesntExist() {
        File file = new File(TEMPORARY);
        // precondition
        assertFalse(file.exists());

        // test
        assertThrows(IOException.class, () -> FileUtils.readAllLines(file));
    }

    @Test
    void testWriteAllLines() throws IOException {
        File file = new File(TEMPORARY);
        // precondition
        assertFalse(file.exists());

        // test simple write
        FileUtils.writeLines(file, TEST_TEXT_LINES);
        assertTrue(file.exists());
        assertEquals(TEST_TEXT_SIZE, file.length());

        // test overwrite
        FileUtils.writeLines(file, Arrays.asList("Hello", "World"));
        assertTrue(file.exists());
        assertEquals("Hello\nWorld\n".length(), file.length());
    }

    //@Test
    void testWriteAllLinesWithFileBlocked() throws IOException {
        File file = new File(TEMPORARY);
        FileUtils.writeLines(file, TEST_TEXT_LINES);
        // precondition
        assertTrue(file.exists());

        // test
        try (FileWriter unused = new FileWriter(file)) {
            assertThrows(IOException.class, () -> FileUtils
                            .writeLines(file, Arrays.asList("Should", "Throw")),
                    () -> "File couldn't be deleted!");
        }
    }

    @Test
    void testResolve() {
        final String directoryName = "a_directory";
        File directory = new File(directoryName);
        final String name = "name";
        File resolvedFile = new File(
                directoryName + File.separator + name);

        assertEquals(resolvedFile, resolve(directory, name));
    }

    @Test
    void testRelativize() {
        File file1 = new File("a/b/c/d");
        File file2 = new File("a/b/");
        File file3 = new File("x/y/z");

        assertEquals(new File("c/d"), FileUtils.relativize(file1, file2));
        assertThrows(IllegalArgumentException.class,
                () -> FileUtils.relativize(file2, file3),
                () -> "Files have different roots!");
    }

    @Test
    void testResolveAndRelativize() {
        File p = new File("a");
        File q = new File("b");
        assertEquals(q, FileUtils.relativize(p, resolve(p, q)));
    }

    @Test
    void testCopy() throws IOException {
        File source = new File(MEDIUM_SIZED_FILE);
        File target = new File(TEMPORARY);

        // precondition
        assertTrue(source.exists());
        assertFalse(target.exists());

        // test
        FileUtils.copy(source, target);
        assertEquals(FileUtils.readAllLines(source),
                FileUtils.readAllLines(target));

        // postcondition
        assertTrue(source.exists());
        assertTrue(target.exists());
    }
}
