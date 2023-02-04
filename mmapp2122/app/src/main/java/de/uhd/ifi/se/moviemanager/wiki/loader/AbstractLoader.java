package de.uhd.ifi.se.moviemanager.wiki.loader;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;

/**
 * Extracts meta data from a given Wiki-page and creates model objects. Some
 * extractions need to be specialized by the subclasses.
 *
 * @param <P> type of the model object created by this loader, i.e. {@link
 *            Movie} or {@link Performer}.
 */
public abstract class AbstractLoader<P> {
    protected List<String> tabularAttributes;

    AbstractLoader(List<String> tabularAttributes) {
        this.tabularAttributes = tabularAttributes;
    }

    /**
     * Loads meta data from the given document and creates a product using this
     * metadata.
     *
     * @param doc document of a Wiki HTML page.
     * @return empty if an exception is thrown during loading, otherwise
     * contains model object.
     */
    public abstract Optional<P> loadDataFromWikiHTML(Document doc);

    /**
     * @param doc document of a Wiki HTML page.
     * @return movie title or performer name parsed from the wiki page.
     */
    public String getTitle(Document doc) {
        return getTable(doc).selectFirst(getNameSelector()).text();
    }

    protected Element getTable(Document doc) {
        return doc.selectFirst(getTableSelector());
    }

    /**
     * @param doc document of a Wiki HTML page.
     * @return image URL parsed from the wiki page.
     */
    public String getImageUrl(Document doc) {
        Element image = getTable(doc).selectFirst("a[class=image]")
                .selectFirst("img");
        String relativePath = image.hasAttr("srcset") ? image
                .attr("srcset") : image.attr("src");
        return "https:" + relativePath.split(" ")[0];
    }

    protected abstract String getTableSelector();

    protected abstract String getNameSelector();

    /**
     * @param doc document of a Wiki HTML page.
     * @return movie description or performer biography parsed from the wiki
     * page.
     */
    protected String getIntroduction(Document doc) {
        int index = doc.selectFirst("div[class~=toc]").elementSiblingIndex();
        Predicate<String> notEmpty = s -> !s.isEmpty();
        return doc.select("p").stream()
                .filter(p -> p.elementSiblingIndex() < index)
                .map(this::removeCites).map(Element::text).filter(notEmpty)
                .limit(1).collect(joining("\n"));
    }

    private Element removeCites(Element e) {
        e.select("sup[id^=cite_ref-]").remove();
        return e;
    }

    protected String getHeader(Element row) {
        Element th = row.selectFirst("th");
        return th.text().toLowerCase();
    }

    protected boolean isHeaderOfInterest(String header) {
        return tabularAttributes.contains(header);
    }

    protected List<String> handleAttributeRow(Element row) {
        Element td = row.selectFirst("td");
        Element hList = td.selectFirst("div[class='hlist hlist-separated']");
        Elements nonListData = td.select("td > div, td > span");

        if (hList != null) {
            return hList.select("li").stream().map(Element::text)
                    .map(String::toLowerCase).collect(toList());
        } else if (nonListData != null && nonListData.size() > 1) {
            return nonListData.stream().map(Element::text).collect(toList());
        } else {
            return singletonList(td.ownText());
        }
    }

    protected Map<String, List<String>> extractAttributesFromTable(
            Element table) {
        List<Element> rows = table.select("th[scope=row]").stream()
                .map(Element::parent).collect(toList());

        return extractAttributesFromTableRows(rows);
    }

    private Map<String, List<String>> extractAttributesFromTableRows(
            List<Element> rows) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Element element : rows) {
            String header = getHeader(element);
            if (isHeaderOfInterest(header)) {
                result.put(header, normContent(element));
            }
        }
        return result;
    }

    protected List<String> normContent(Element content) {
        return handleAttributeRow(content);
    }
}