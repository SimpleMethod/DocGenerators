package pl.mlodawski.docgenerator.docmanager.richtext;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.Stack;

/*
 @Author Michał Młodawski
 */
/**
 * Interface for processing text with HTML-like tags and applying styles
 * to Apache POI objects such as XWPFParagraph and XWPFRun.
 * Implementations of this interface are responsible for parsing text
 * with embedded tags and transforming it into styled document elements.
 */
@Deprecated(since = "1.0.0", forRemoval = true)
public interface IHtmlTextProcessor {

    /**
     * Parses the provided text for HTML-like tags and applies the corresponding
     * styles to the given XWPFParagraph.
     *
     * @param paragraph  the XWPFParagraph object where the text and styles will be added
     * @param text       the string containing text with HTML-like tags to be processed
     */
    void readParagraph(XWPFParagraph paragraph, String text);

    /**
     * Parses the provided text for HTML-like tags and applies the corresponding
     * styles to the given XWPFParagraph, using an external stack to track active tags.
     *
     * @param paragraph  the XWPFParagraph object where the text and styles will be added
     * @param text       the string containing text with HTML-like tags to be processed
     * @param activeTags a stack of active HTML-like tags used to track styles during parsing
     */
    void readParagraph(XWPFParagraph paragraph, String text, Stack<String> activeTags);

    /**
     * Applies text styling based on active HTML-like tags and sets the text content
     * for the specified XWPFRun object.
     *
     * @param xwpfRun    the XWPFRun object where the styles and text will be applied
     * @param activeTags a stack of active HTML-like tags that determine the styles to apply
     * @param text       the text content to be set in the XWPFRun object
     */
    void setStyleForText(XWPFRun xwpfRun, Stack<String> activeTags, String text);
}
