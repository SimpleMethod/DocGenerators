package pl.mlodawski.docgenerator.docmanager.paragraphs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/*
 @Author Michał Młodawski
 */
/**
 * Represents a text style configuration that can be applied to a paragraph or text content.
 *
 * The Style class encapsulates various styling properties such as font, font size, text color,
 * background color, alignment, line spacing, and text decorations (bold, italic, underline, etc.).
 * This enables consistent styling of text content according to specific requirements.
 *
 * Constructors:
 * - Default constructor initializes a Style object with default values.
 * - Parameterized constructor allows for explicit initialization of all style attributes.
 *
 * Properties:
 * - font: Specifies the font family of the text (e.g., "Arial").
 * - fontSize: Represents the size of the font in points (e.g., 12.0).
 * - color: Defines the text color in a hexadecimal or named color format.
 * - backgroundColor: Specifies the background color of the text in a hexadecimal or named color format.
 * - alignment: Indicates the alignment of the text using predefined values from the AlignmentType enum.
 * - lineSpacing: Sets the spacing between lines of text, specified as a double value.
 * - bold: Determines whether the text is bolded. Default is false.
 * - italic: Indicates whether the text is italicized. Default is false.
 * - underline: Determines the type of underline using values from the UnderlineType enum.
 * - strikeThrough: Specifies if the text has a strikethrough decoration. Default is false.
 * - subscript: Indicates if the text is displayed as subscript. Default is false.
 * - superscript: Indicates if the text is displayed as superscript. Default is false.
 */
@Data
public class Style {
    private String font;
    private Double fontSize;
    private String color;
    private String backgroundColor;
    private AlignmentType alignment;
    private Double lineSpacing;
    private Boolean bold = false;
    private Boolean italic = false;
    private UnderlineType underline = UnderlineType.NONE;
    private Boolean strikeThrough = false;
    private Boolean subscript = false;
    private Boolean superscript = false;

    public Style() {
    }

    /**
     * Constructs a Style object with the specified properties.
     *
     * @param font the font of the text.
     * @param fontSize the size of the font.
     * @param color the color of the text in hexadecimal or named color format.
     * @param backgroundColor the background color of the text in hexadecimal or named color format.
     * @param alignment the alignment of the text, specified as an AlignmentType.
     * @param lineSpacing the line spacing of the text, specified as a double.
     * @param bold a boolean value indicating whether the text should be bold.
     * @param italic a boolean value indicating whether the text should be italic.
     * @param underline the type of underline to apply to the text, specified as an UnderlineType.
     * @param strikeThrough a boolean value indicating whether the text should have a strikethrough.
     * @param subscript a boolean value indicating whether the text should be subscript.
     * @param superscript a boolean value indicating whether the text should be superscript.
     */
    @JsonCreator
    public Style(@JsonProperty("font") String font,
                 @JsonProperty("fontSize") Double fontSize,
                 @JsonProperty("color") String color,
                 @JsonProperty("backgroundColor") String backgroundColor,
                 @JsonProperty("alignment") AlignmentType alignment,
                 @JsonProperty("lineSpacing") Double lineSpacing,
                 @JsonProperty("bold") Boolean bold,
                 @JsonProperty("italic") Boolean italic,
                 @JsonProperty("underline") UnderlineType underline,
                 @JsonProperty("strikeThrough") Boolean strikeThrough,
                 @JsonProperty("subscript") Boolean subscript,
                 @JsonProperty("superscript") Boolean superscript) {
        this.font = font;
        this.fontSize = fontSize;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.alignment = alignment;
        this.lineSpacing = lineSpacing;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
        this.subscript = subscript;
        this.superscript = superscript;
    }
}

