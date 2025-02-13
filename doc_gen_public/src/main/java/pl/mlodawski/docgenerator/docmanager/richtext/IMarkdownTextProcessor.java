package pl.mlodawski.docgenerator.docmanager.richtext;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/*
 @Author Michał Młodawski
 */
/**
 * Interface for processing markdown text and transforming it into formatted content
 * within a Word document. The implementation of this interface should parse
 * markdown syntax and apply the corresponding styles to the provided Apache POI
 * XWPFParagraph object.
 *
 * Supported markdown syntax includes:
 * - `**` or `__` for bold text.
 * - `*` or `_` for italic text.
 * - `~~` for strikethrough text.
 * - `[text](url)` for hyperlinks.
 */
public interface IMarkdownTextProcessor {

    /**
     * Processes markdown text and applies formatting to a Word document paragraph.
     * Supported markdown syntax:
     * - ** or __ for bold text
     * - * or _ for italic text
     * - ~~ for strikethrough text
     * - [text](url) for hyperlinks
     *
     * @param paragraph XWPFParagraph object where the formatted text will be added
     * @param text markdown text to be processed and formatted
     */
    void processMarkdown(XWPFParagraph paragraph, String text);

}
