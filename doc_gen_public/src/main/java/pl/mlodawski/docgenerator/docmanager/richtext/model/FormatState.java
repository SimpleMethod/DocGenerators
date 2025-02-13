package pl.mlodawski.docgenerator.docmanager.richtext.model;

/*
 @Author Michał Młodawski
 */
/**
 * Represents the formatting state of text with properties for bold, italic,
 * and strike-through styles. This class provides mechanisms for initializing
 * and copying the formatting states.
 */
public class FormatState {
    public boolean bold;
    public boolean italic;
    public boolean strike;

    public FormatState() {
        this.bold = false;
        this.italic = false;
        this.strike = false;
    }

    public FormatState(FormatState other) {
        this.bold = other.bold;
        this.italic = other.italic;
        this.strike = other.strike;
    }
}