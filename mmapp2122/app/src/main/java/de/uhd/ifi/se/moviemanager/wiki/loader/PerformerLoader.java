package de.uhd.ifi.se.moviemanager.wiki.loader;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.util.Log;

/**
 * Singleton of the performer loader which is responsible for the data
 * extraction for performers.
 */
public final class PerformerLoader extends AbstractLoader<Performer> {
    private static final String BORN = "born";
    private static final String OCCUPATION = "occupation";
    public static final List<String> TABULAR_ATTRIBUTES = unmodifiableList(
            asList(BORN, OCCUPATION));
    private static final Pattern DATE_PATTERN = Pattern
            .compile("\\((\\d{4})-(\\d{2})-(\\d{2})\\)");
    private static final PerformerLoader INSTANCE = new PerformerLoader();
    private static final String NAME_SELECTOR = "div[class=fn]";
    private static final String TABLE_SELECTOR = "table[class=infobox " +
            "biography vcard]";

    private PerformerLoader() {
        super(TABULAR_ATTRIBUTES);
    }

    /**
     * @return the only PerformerLoader instance available (Singleton object).
     */
    public static PerformerLoader getInstance() {
        return INSTANCE;
    }

    private static Date convertDate(Matcher regex) {
        regex.find();
        Date date = new Date();
        try {
            String year = regex.group(1);
            String month = regex.group(2);
            String day = regex.group(3);
            Calendar calendar = new GregorianCalendar(parseInt(year),
                    parseInt(month) - 1, parseInt(day));
            date = calendar.getTime();
        } catch (Exception e) {
            Log.e("PerformerLoader", e.getMessage());
        }
        return date;
    }

    @Override
    public Optional<Performer> loadDataFromWikiHTML(Document doc) {
        if (doc == null || getTable(doc) == null) {
            return Optional.empty();
        }
        Performer performerFromWiki = new Performer(getTitle(doc));
        performerFromWiki.setImage(getImageUrl(doc));
        performerFromWiki.setBiography(getIntroduction(doc));
        Map<String, List<String>> attributes = extractAttributesFromTable(
                getTable(doc));

        List<String> born = attributes.get(BORN);
        if (DATE_PATTERN.matcher(born.get(0)).matches()) {
            performerFromWiki.setDateOfBirth(getDateOfBirth(born.get(0)));
        } else {
            performerFromWiki.setBirthName(born.get(0));
            performerFromWiki.setDateOfBirth(getDateOfBirth(born.get(1)));
        }
        performerFromWiki.setOccupations(
                attributes.getOrDefault(OCCUPATION, new ArrayList<>()));
        return Optional.of(performerFromWiki);
    }

    private Date getDateOfBirth(String date) {
        return convertDate(DATE_PATTERN.matcher(date));
    }

    @Override
    protected String getNameSelector() {
        return NAME_SELECTOR;
    }

    @Override
    protected String getTableSelector() {
        return TABLE_SELECTOR;
    }
}
