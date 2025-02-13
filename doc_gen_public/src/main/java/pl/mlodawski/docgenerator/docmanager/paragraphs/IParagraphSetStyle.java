package pl.mlodawski.docgenerator.docmanager.paragraphs;

/*
 @Author Michał Młodawski
 */

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;

/**
 * Interface defining methods for setting paragraph styles.
 */
public interface IParagraphSetStyle {

    IParagraphSetStyle setFontFamily(String fontFamily);

    IParagraphSetStyle setFontSize(Integer fontSize);

    IParagraphSetStyle setFontColor(String setColor);

    IParagraphSetStyle setFontBold(Boolean value);

    IParagraphSetStyle setFontItalic(Boolean value);

    IParagraphSetStyle setText(String text);

    IParagraphSetStyle appendText(String text);

    IParagraphSetStyle setUnderline(UnderlinePatterns pattern);

    IParagraphSetStyle setSuperScript();

    IParagraphSetStyle setStrikeThrough(boolean value);

    IParagraphSetStyle setBackgroundColor(String color);
}
