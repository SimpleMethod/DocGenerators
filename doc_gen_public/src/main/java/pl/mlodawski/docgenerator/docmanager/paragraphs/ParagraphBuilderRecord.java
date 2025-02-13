package pl.mlodawski.docgenerator.docmanager.paragraphs;


import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;

/*
 @Author Michał Młodawski
 */
/**
 * The ParagraphBuilderRecord class provides a fluent API for setting various style
 * properties of a text paragraph within a document. It operates on an instance of
 * XWPFRun from Apache POI to apply formatting and styling attributes to the text.
 * This class implements the IParagraphSetStyle interface and allows for chaining of methods.
 */
public record ParagraphBuilderRecord(XWPFRun xwpfRun) implements IParagraphSetStyle {

    /**
     * Create builder entry point for paragraph
     *
     * @return Builder class
     */

    public ParagraphBuilderRecord setStyleEntryPoint() {
        this.setFontBold(false);
        this.setFontItalic(false);
        this.setFontFamily("Arial");
        this.setFontColor("000000");
        this.setFontSize(11);
        return this;
    }

    /**
     * Set font family for paragraph
     *
     * @param fontFamily Name of font using in paragraph
     * @return Builder class
     */
    public ParagraphBuilderRecord setFontFamily(final String fontFamily) {
        xwpfRun.setFontFamily(fontFamily);
        return this;
    }

    /**
     * Set font size for paragraph
     *
     * @param fontSize Number of font sizes
     * @return Builder class
     */
    public ParagraphBuilderRecord setFontSize(final Integer fontSize) {
        xwpfRun.setFontSize(fontSize);
        return this;
    }

    /**
     * Set color of font in paragraph
     *
     * @param setColor Color in hex format
     * @return Builder class
     */
    public ParagraphBuilderRecord setFontColor(final String setColor) {
        xwpfRun.setColor(setColor);
        return this;
    }

    /**
     * Set bold for paragraph
     *
     * @param value Add of remove bold of a text in paragraph
     * @return Builder class
     */
    public ParagraphBuilderRecord setFontBold(final Boolean value) {
        xwpfRun.setBold(value);
        return this;
    }

    /**
     * Set italic for paragraph
     *
     * @param value Add of remove italic of a text in paragraph
     * @return Builder class
     */
    public ParagraphBuilderRecord setFontItalic(final Boolean value) {
        xwpfRun.setItalic(value);
        return this;
    }

    /**
     * Set text in paragraph
     *
     * @param text Text to insert in paragraph
     * @return Builder class
     */
    public ParagraphBuilderRecord setText(final String text) {
        xwpfRun.setText(text, 0);
        return this;
    }

    /**
     * Append text into paragraph
     *
     * @param text Text to append into paragraph
     * @return Builder class
     */
    public ParagraphBuilderRecord appendText(final String text) {
        xwpfRun.setText(text);
        return this;
    }

    /**
     * Set underline for text in paragraph
     *
     * @param pattern Pattern of underline
     * @return Builder class
     */
    public ParagraphBuilderRecord setUnderline(final UnderlinePatterns pattern) {
        xwpfRun.setUnderline(pattern);
        return this;
    }

    /**
     * Set superscript for text in paragraph
     *
     * @return Builder class
     */
    public ParagraphBuilderRecord setSuperScript() {
        xwpfRun.setSubscript(VerticalAlign.SUPERSCRIPT);
        return this;
    }

    /**
     * Set strike for text in paragraph
     *
     * @param value Add of remove strike of a text in paragraph
     * @return Builder class
     */
    public ParagraphBuilderRecord setStrikeThrough(final boolean value) {
        xwpfRun.setStrikeThrough(value);
        return this;
    }

    /**
     * Set background color for text in paragraph
     *
     * @param color Color in hex format
     * @return Builder class
     */
    public ParagraphBuilderRecord setBackgroundColor(final String color) {
        XmlCursor cursor = xwpfRun.getCTR().newCursor();
        cursor.selectPath("./*");
        if (cursor.toNextSelection()) {
            cursor.beginElement("w:shd");
            cursor.insertAttributeWithValue("w:fill", color);
        }
        return this;
    }

}