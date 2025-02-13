package pl.mlodawski.docgenerator.docmanager.richtext;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 @Author Michał Młodawski
 */
/**
 * This service is responsible for processing text with HTML-like tags and applying
 * the corresponding styles to Apache POI objects such as {@link XWPFParagraph} and {@link XWPFRun}.
 */
@Service
@Deprecated(since = "1.0", forRemoval = true)
public class HtmlTextService implements IHtmlTextProcessor {


    /**
     * Applies text styling based on active HTML-like tags and sets the text content
     * for the specified {@link XWPFRun} object.
     *
     * @param xwpfRun the {@link XWPFRun} object where the styles and text will be applied
     * @param activeTags a stack of active HTML-like tags that determine the styles to apply
     * @param text the text content to be set in the {@link XWPFRun} object
     */
    @ServiceOperation("HtmlTextService.setStyleForText")
    public void setStyleForText(XWPFRun xwpfRun, Stack<String> activeTags, String text) {
        if (activeTags.contains("b")) {
            xwpfRun.setBold(true);
        }
        if (activeTags.contains("i")) {
            xwpfRun.setItalic(true);
        }
        if (activeTags.contains("del")) {
            xwpfRun.setStrike(true);
        }
        xwpfRun.setText(text);
    }

    /**
     * Parses the provided text for HTML-like tags and applies the corresponding
     * styles to the given XWPFParagraph. The method uses a stack to manage
     * active tags, which determine the styles to be applied to the text.
     *
     * @param paragraph the XWPFParagraph object where the text and styles will be added
     * @param text the string containing text with HTML-like tags to be processed
     * @param activeTags a stack of active HTML-like tags used to track styles during parsing
     */
    @ServiceOperation("HtmlTextService.readParagraph")
    public void readParagraph(XWPFParagraph paragraph, String text, Stack<String> activeTags) {
        String regex = "<(/?\\w+)>|([^<]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                String tag = matcher.group(1);
                if (tag.startsWith("/")) {
                    if (!activeTags.isEmpty()) {
                        activeTags.pop();
                    }
                } else {
                    activeTags.push(tag);
                }
            } else if (matcher.group(2) != null) {
                XWPFRun run = paragraph.createRun();
                setStyleForText(run, activeTags, matcher.group(2));
            }
        }
    }

    /**
     * Parses a given text string with HTML-like tags and applies the corresponding
     * styles to the specified XWPFParagraph. Automatically manages an empty stack
     * of active tags for styling.
     *
     * @param paragraph the XWPFParagraph object where the text and styles will be applied
     * @param text the string with text and HTML-like tags to be processed and styled
     */
    public void readParagraph(XWPFParagraph paragraph, String text) {
        readParagraph(paragraph, text, new Stack<>());
    }
}


