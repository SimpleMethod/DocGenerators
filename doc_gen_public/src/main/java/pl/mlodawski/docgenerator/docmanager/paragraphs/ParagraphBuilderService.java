package pl.mlodawski.docgenerator.docmanager.paragraphs;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.springframework.stereotype.Service;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;
/*
 @Author Michał Młodawski
 */
/**
 * The ParagraphBuilderService class provides a fluent API for setting various style
 * properties of a text paragraph within a document. It operates on an instance of
 * XWPFRun from Apache POI to apply formatting and styling attributes to the text.
 * This class implements the IParagraphSetStyle interface and allows for chaining of methods.
 */
@Service
@Slf4j
public class ParagraphBuilderService {

    /**
     * Create builder entry point for paragraph and applies default styling
     *
     * @param xwpfRun The XWPFRun instance to be styled
     */
    @ServiceOperation("ParagraphBuilderService.setStyleEntryPoint")
    public void setStyleEntryPoint(XWPFRun xwpfRun) {
        log.debug("Setting entry point style for paragraph");
        setFontBold(xwpfRun, false);
        setFontItalic(xwpfRun, false);
        setFontFamily(xwpfRun, "Arial");
        setFontColor(xwpfRun, "000000");
        setFontSize(xwpfRun, 11);
    }

    /**
     * Set font family for paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     * @param fontFamily Name of font to use in paragraph
     */
    @ServiceOperation("ParagraphBuilderService.setFontFamily")
    public void setFontFamily(XWPFRun xwpfRun, final String fontFamily) {
        log.debug("Setting font family: {}", fontFamily);
        xwpfRun.setFontFamily(fontFamily);
    }

    /**
     * Set font size for paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     * @param fontSize Font size to set
     */
    @ServiceOperation("ParagraphBuilderService.setFontSize")
    public void setFontSize(XWPFRun xwpfRun, final Integer fontSize) {
        log.debug("Setting font size: {}", fontSize);
        xwpfRun.setFontSize(fontSize);
    }

    /**
     * Set color of font in paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     * @param setColor Color in hex format
     */
    @ServiceOperation("ParagraphBuilderService.setFontColor")
    public void setFontColor(XWPFRun xwpfRun, final String setColor) {
        log.debug("Setting font color: {}", setColor);
        xwpfRun.setColor(setColor);
    }

    /**
     * Set bold for paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     * @param value Add or remove bold from text
     */
    @ServiceOperation("ParagraphBuilderService.setFontBold")
    public void setFontBold(XWPFRun xwpfRun, final Boolean value) {
        log.debug("Setting bold: {}", value);
        xwpfRun.setBold(value);
    }

    /**
     * Set italic for paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     * @param value Add or remove italic from text
     */
    @ServiceOperation("ParagraphBuilderService.setFontItalic")
    public void setFontItalic(XWPFRun xwpfRun, final Boolean value) {
        log.debug("Setting italic: {}", value);
        xwpfRun.setItalic(value);
    }

    /**
     * Set text in paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     * @param text Text to insert
     */
    @ServiceOperation("ParagraphBuilderService.setText")
    public void setText(XWPFRun xwpfRun, final String text) {
        log.debug("Setting text: {}", text);
        xwpfRun.setText(text, 0);
    }

    /**
     * Append text to paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     * @param text Text to append
     */
    @ServiceOperation("ParagraphBuilderService.appendText")
    public void appendText(XWPFRun xwpfRun, final String text) {
        log.debug("Appending text: {}", text);
        xwpfRun.setText(text);
    }

    /**
     * Set underline for text in paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     * @param pattern Underline pattern to apply
     */
    @ServiceOperation("ParagraphBuilderService.setUnderline")
    public void setUnderline(XWPFRun xwpfRun, final UnderlinePatterns pattern) {
        log.debug("Setting underline pattern: {}", pattern);
        xwpfRun.setUnderline(pattern);
    }

    /**
     * Set superscript for text in paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     */
    @ServiceOperation("ParagraphBuilderService.setSuperScript")
    public void setSuperScript(XWPFRun xwpfRun) {
        log.debug("Setting superscript");
        xwpfRun.setSubscript(VerticalAlign.SUPERSCRIPT);
    }

    /**
     * Set strike-through for text in paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     * @param value Add or remove strike-through
     */
    @ServiceOperation("ParagraphBuilderService.setStrikeThrough")
    public void setStrikeThrough(XWPFRun xwpfRun, final boolean value) {
        log.debug("Setting strike-through: {}", value);
        xwpfRun.setStrikeThrough(value);
    }

    /**
     * Set background color for text in paragraph
     *
     * @param xwpfRun The XWPFRun instance to be modified
     * @param color Color in hex format
     */
    @ServiceOperation("ParagraphBuilderService.setBackgroundColor")
    public void setBackgroundColor(XWPFRun xwpfRun, final String color) {
        log.debug("Setting background color: {}", color);
        XmlCursor cursor = xwpfRun.getCTR().newCursor();
        cursor.selectPath("./*");
        if (cursor.toNextSelection()) {
            cursor.beginElement("w:shd");
            cursor.insertAttributeWithValue("w:fill", color);
        }
    }
}