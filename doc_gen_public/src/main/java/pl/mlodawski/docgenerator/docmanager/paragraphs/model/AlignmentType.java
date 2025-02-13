package pl.mlodawski.docgenerator.docmanager.paragraphs.model;


/*
 @Author Michał Młodawski
 */
/**
 * Represents the alignment options for text within a paragraph.
 *
 * The AlignmentType enum specifies various text alignment configurations that can be
 * applied to paragraphs. These configurations determine how text is oriented
 * horizontally within the boundaries of a paragraph.
 *
 * Available alignment types:
 * - LEFT: Aligns text to the left margin.
 * - CENTER: Centers text horizontally.
 * - RIGHT: Aligns text to the right margin.
 * - BOTH: Justifies the text, aligning it to both left and right margins.
 * - MEDIUM_KASHIDA: Adds medium Kashida for justification in certain scripts (e.g., Arabic).
 * - DISTRIBUTE: Distributes text evenly across the line.
 * - NUM_TAB: Aligns numerical characters in a tabular format.
 * - HIGH_KASHIDA: Adds high Kashida for justification in certain scripts.
 * - LOW_KASHIDA: Adds low Kashida for justification in certain scripts.
 * - THAI_DISTRIBUTE: Distributes text according to Thai language typography rules.
 */
 enum AlignmentType {
    LEFT,
    CENTER,
    RIGHT,
    BOTH,
    MEDIUM_KASHIDA,
    DISTRIBUTE,
    NUM_TAB,
    HIGH_KASHIDA,
    LOW_KASHIDA,
    THAI_DISTRIBUTE
}
