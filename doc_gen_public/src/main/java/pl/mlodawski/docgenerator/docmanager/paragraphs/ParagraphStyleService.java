package pl.mlodawski.docgenerator.docmanager.paragraphs;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STVerticalAlignRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalAlignRun;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import pl.mlodawski.docgenerator.docmanager.paragraphs.model.Style;
import pl.mlodawski.docgenerator.docmanager.paragraphs.model.UnderlineType;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.StringReader;

/*
 @Author Michał Młodawski
 */
 /**
  * Provides functionality for applying, extracting, and parsing paragraph styles in documents using
  * the Apache POI XWPF library.
  * This service enables transformations between {@code XWPFRun} objects and custom {@code Style} objects
  * for streamlined style management.
  */
 public class ParagraphStyleService {

    /**
     * Applies the specified style to the given {@code XWPFRun} instance by setting
     * various text attributes such as font, size, color, bold, italic, underline,
     * strike-through, subscript, and superscript.
     *
     * @param run The {@code XWPFRun} instance to which the style should be applied. Must be non-null.
     * @param style The {@code Style} object containing the desired text attributes to apply. Must be non-null.
     */
    @ServiceOperation("ParagraphStyleService.applyStyle")
    public void applyStyle(XWPFRun run, Style style) {

        // Set font
        if (style.getFont() != null) {
            run.setFontFamily(style.getFont());
        }

        // Set font size
        if (style.getFontSize() != null) {
            run.setFontSize(style.getFontSize()/2);
        }

        // Set color
        if (style.getColor() != null) {
            run.setColor(style.getColor());
        }

        // Set bold
        if (style.getBold() != null) {
            run.setBold(style.getBold());
        }

        // Set italic
        if (style.getItalic() != null) {
            run.setItalic(style.getItalic());
        }

        // Set underline
        if (style.getUnderline() != null) {
            switch (style.getUnderline()) {
                case SINGLE:
                    run.setUnderline(UnderlinePatterns.SINGLE);
                    break;
                case DOUBLE:
                    run.setUnderline(UnderlinePatterns.DOUBLE);
                    break;
                case THICK:
                    run.setUnderline(UnderlinePatterns.THICK);
                    break;
                case DOTTED:
                    run.setUnderline(UnderlinePatterns.DOTTED);
                    break;
                case DASHED:
                    run.setUnderline(UnderlinePatterns.DASH);
                    break;
                case DOT_DASH:
                    run.setUnderline(UnderlinePatterns.DOT_DASH);
                    break;
                case DOT_DOT_DASH:
                    run.setUnderline(UnderlinePatterns.DOT_DOT_DASH);
                    break;
                case WAVY:
                    run.setUnderline(UnderlinePatterns.WAVE);
                    break;
                case NONE:
                default:
                    run.setUnderline(UnderlinePatterns.NONE);
            }
        }

        // Set strikeThrough
        if (style.getStrikeThrough() != null) {
            run.setStrikeThrough(style.getStrikeThrough());
        }

        // Set subscript
        if (style.getSubscript() != null) {
            run.setSubscript(style.getSubscript() ? VerticalAlign.SUBSCRIPT : VerticalAlign.BASELINE);
        }

        // Set superscript
        if (style.getSuperscript() != null) {
            run.setSubscript(style.getSuperscript() ? VerticalAlign.SUPERSCRIPT : VerticalAlign.BASELINE);
        }

    }

    /**
     * Extracts the style properties of the given {@code XWPFRun} and constructs a {@code Style} object.
     * The method retrieves font attributes, size, color, bold, italic, underline, strike-through,
     * superscript, and subscript properties, as well as vertical alignment specifics.
     *
     * @param run The {@code XWPFRun} instance from which style properties are extracted. Must be non-null.
     * @return A {@code Style} object populated with the extracted style properties from the provided {@code XWPFRun}.
     */
    @ServiceOperation("ParagraphStyleService.getStyle")
    public static Style getStyle(XWPFRun run) {
        Style style = new Style();

        style.setFont(run.getFontFamily());
        style.setFontSize(run.getFontSizeAsDouble());

        String rgbColor = run.getColor();
        if (rgbColor != null && rgbColor.length() == 6) {
            int red = Integer.parseInt(rgbColor.substring(0, 2), 16);
            int green = Integer.parseInt(rgbColor.substring(2, 4), 16);
            int blue = Integer.parseInt(rgbColor.substring(4, 6), 16);
            Color color = new Color(red, green, blue);
            style.setColor(String.valueOf(color));
        }

        style.setBold(run.isBold());
        style.setItalic(run.isItalic());
        style.setStrikeThrough(run.isStrikeThrough());

        CTR ctr = run.getCTR();
        if (ctr != null) {
            CTRPr rPr = ctr.getRPr();
            if (rPr != null) {
                CTVerticalAlignRun[] vertAlignArray = rPr.getVertAlignArray();
                if (vertAlignArray.length > 0) {
                    CTVerticalAlignRun vertAlign = vertAlignArray[0];
                    STVerticalAlignRun.Enum valignEnum = vertAlign.getVal();
                    if (valignEnum != null) {
                        switch (valignEnum.intValue()) {
                            case STVerticalAlignRun.INT_SUPERSCRIPT:
                                style.setSuperscript(true);
                                break;
                            case STVerticalAlignRun.INT_SUBSCRIPT:
                                style.setSubscript(true);
                                break;
                            default:
                                style.setSuperscript(false);
                                style.setSubscript(false);
                        }
                    }
                }
            }
        }

        UnderlinePatterns underlinePattern = run.getUnderline();
        UnderlineType underlineType = UnderlineType.valueOf(underlinePattern.name());
        style.setUnderline(underlineType);

        return style;
    }

    /**
     * Parses the given XML string representing a "getCTR" and converts it into a {@code Style} object.
     * Extracts various style attributes such as font, font size, color, background color, and others.
     *
     * @param getCTR A non-null, non-empty XML string containing the "getCTR" data to be parsed.
     *
     * @return A {@code Style} object populated with the extracted style properties.
     *
     * @throws IllegalArgumentException if the {@code getCTR} input is null or empty.
     * @throws Exception if an error occurs during XML parsing.
     */
    @ServiceOperation("ParagraphStyleService.parseGetCTRToStyle")
    public Style parseGetCTRToStyle(String getCTR) throws Exception {
        if (getCTR == null || getCTR.isEmpty()) {
            throw new IllegalArgumentException("getCTR cannot be null or empty");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(getCTR)));

        doc.getDocumentElement().normalize();

        NodeList list = doc.getElementsByTagName("w:rPr");

        Style style = new Style();

        for (int temp = 0; temp < list.getLength(); temp++) {
            Node node = list.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getElementsByTagName("w:rFonts").getLength() > 0 && element.getElementsByTagName("w:rFonts").item(0).getAttributes().getNamedItem("w:ascii") != null) {
                    style.setFont(element.getElementsByTagName("w:rFonts").item(0).getAttributes().getNamedItem("w:ascii").getNodeValue());
                }

                if (element.getElementsByTagName("w:b").getLength() > 0) {
                    style.setBold(true);
                }

                if (element.getElementsByTagName("w:color").getLength() > 0 && element.getElementsByTagName("w:color").item(0).getAttributes().getNamedItem("w:val") != null) {
                    style.setColor("#" + element.getElementsByTagName("w:color").item(0).getAttributes().getNamedItem("w:val").getNodeValue());
                }

                if (element.getElementsByTagName("w:sz").getLength() > 0 && element.getElementsByTagName("w:sz").item(0).getAttributes().getNamedItem("w:val") != null) {
                    style.setFontSize(Double.valueOf(element.getElementsByTagName("w:sz").item(0).getAttributes().getNamedItem("w:val").getNodeValue()));
                }

                if (element.getElementsByTagName("w:shd").getLength() > 0 && element.getElementsByTagName("w:shd").item(0).getAttributes().getNamedItem("w:fill") != null) {
                    style.setBackgroundColor("#" + element.getElementsByTagName("w:shd").item(0).getAttributes().getNamedItem("w:fill").getNodeValue());
                }

                if (element.getElementsByTagName("w:vertAlign").getLength() > 0 && element.getElementsByTagName("w:vertAlign").item(0).getAttributes().getNamedItem("w:val") != null) {
                    if ("superscript".equals(element.getElementsByTagName("w:vertAlign").item(0).getAttributes().getNamedItem("w:val").getNodeValue())) {
                        style.setSuperscript(true);
                    } else if ("subscript".equals(element.getElementsByTagName("w:vertAlign").item(0).getAttributes().getNamedItem("w:val").getNodeValue())) {
                        style.setSubscript(true);
                    }
                }

                if (element.getElementsByTagName("w:i").getLength() > 0) {
                    style.setItalic(true);
                }

                if (element.getElementsByTagName("w:u").getLength() > 0 && element.getElementsByTagName("w:u").item(0).getAttributes().getNamedItem("w:val") != null) {
                    String underlineValue = element.getElementsByTagName("w:u").item(0).getAttributes().getNamedItem("w:val").getNodeValue();
                    style.setUnderline(UnderlineType.valueOf(underlineValue.toUpperCase()));
                }

                if (element.getElementsByTagName("w:strike").getLength() > 0) {
                    style.setStrikeThrough(true);
                }
            }
        }

        return style;
    }
}
