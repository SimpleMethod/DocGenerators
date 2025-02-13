package pl.mlodawski.docgenerator.docmanager.paragraphs.model;

/*
 @Author Michał Młodawski
 */
/**
 * Represents the types of underline styles that can be applied to text.
 *
 * This enum defines various underline types which can be used to format text in
 * different styles. Each type corresponds to a specific visual representation of underlined text.
 *
 * Available underline types:
 * - NONE: No underline applied.
 * - SINGLE: A single continuous underline.
 * - DOUBLE: Two continuous underlines.
 * - THICK: A thick continuous underline.
 * - DOTTED: A dotted underline.
 * - DASHED: A dashed underline.
 * - DOT_DASH: A repeating dot-dash pattern underline.
 * - DOT_DOT_DASH: A repeating dot-dot-dash pattern underline.
 * - WAVY: A wavy line underline.
 */
public enum UnderlineType {
    NONE,
    SINGLE,
    DOUBLE,
    THICK,
    DOTTED,
    DASHED,
    DOT_DASH,
    DOT_DOT_DASH,
    WAVY
}
