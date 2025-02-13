package pl.mlodawski.docgenerator.docmanager.richtext;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.springframework.stereotype.Service;
import pl.mlodawski.docgenerator.docmanager.richtext.model.FormatState;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;

/*
 @Author Michał Młodawski
 */
/**
 * Service class for processing markdown-like text and applying formatting in XWPFParagraph objects.
 * Supports bold, italic, strikethrough styles, and hyperlinks using markdown-inspired syntax.
 */
@Service
public class MarkdownTextService implements IMarkdownTextProcessor {

    /**
     * Processes the given markdown text and applies formatting to the specified paragraph.
     *
     * @param paragraph the paragraph in which the markdown text will be processed
     * @param text      the markdown text to be processed and formatted
     */
    @ServiceOperation("MarkdownTextService.processMarkdown")
    public void processMarkdown(XWPFParagraph paragraph, String text) {
        processWithState(paragraph, text, new FormatState(), 0);
    }

    /**
     * Processes a given text within a paragraph, applying specified formatting states
     * and handling markdown-inspired markers such as bold, italic, strikethrough, and links.
     *
     * @param paragraph the XWPFParagraph object where processed text will be applied
     * @param text      the input text to be processed for formatting
     * @param state     the current format state containing style attributes like bold, italic, or strike-through
     * @param startPos  the starting position in the text for processing
     * @return the updated position in the text after processing all relevant characters
     */
    @ServiceOperation("MarkdownTextService.processWithState")
    private int processWithState(XWPFParagraph paragraph, String text, FormatState state, int startPos) {
        StringBuilder currentText = new StringBuilder();
        int i = startPos;

        while (i < text.length()) {
            char c = text.charAt(i);

            if (c == '*' || c == '_' || c == '~' || c == '[') {
                // Dodaj zgromadzony tekst przed znacznikiem
                if (currentText.length() > 0) {
                    addFormattedRun(paragraph, currentText.toString(), state);
                    currentText.setLength(0);
                }

                if ((c == '*' || c == '_') && i + 1 < text.length() && text.charAt(i + 1) == c) {
                    // Pogrubienie
                    FormatState newState = new FormatState(state);
                    newState.bold = !state.bold;
                    i = processBetweenMarkers(paragraph, text, i + 2, "" + c + c, newState);
                } else if (c == '*' || c == '_') {
                    // Kursywa
                    FormatState newState = new FormatState(state);
                    newState.italic = !state.italic;
                    i = processBetweenMarkers(paragraph, text, i + 1, "" + c, newState);
                } else if (c == '~' && i + 1 < text.length() && text.charAt(i + 1) == '~') {
                    // Przekreślenie
                    FormatState newState = new FormatState(state);
                    newState.strike = !state.strike;
                    i = processBetweenMarkers(paragraph, text, i + 2, "~~", newState);
                } else if (c == '[') {
                    // Link
                    i = processLink(paragraph, text, i, state);
                }
            } else {
                currentText.append(c);
                i++;
            }
        }

        if (currentText.length() > 0) {
            addFormattedRun(paragraph, currentText.toString(), state);
        }

        return i;
    }

    /**
     * Processes the text within specified markers in the given paragraph, applying formatting
     * based on the provided state and returns the position after the closing marker.
     *
     * @param paragraph the XWPFParagraph to which the processed text will be added
     * @param text      the complete text being processed
     * @param startPos  the starting position within the text to begin processing
     * @param marker    the marker used to identify the beginning and end of the text section
     * @param state     the FormatState containing formatting settings to apply within the markers
     * @return the position after the closing marker, or the original start position if the closing marker is not found
     */
    @ServiceOperation("MarkdownTextService.processBetweenMarkers")
    private int processBetweenMarkers(XWPFParagraph paragraph, String text, int startPos, String marker, FormatState state) {
        int endPos = findClosingMarker(text, startPos, marker);
        if (endPos == -1) {
            addFormattedRun(paragraph, marker, new FormatState());
            return startPos;
        }

        processWithState(paragraph, text.substring(startPos, endPos), state, 0);
        return endPos + marker.length();
    }

    /**
     * Processes a potential hyperlink in the given text, starting at the specified position,
     * and adds it as a hyperlink to the provided paragraph if valid.
     *
     * @param paragraph the XWPFParagraph object to which the hyperlink will be added
     * @param text      the text containing the potential hyperlink to be processed
     * @param startPos  the starting position in the text to look for a hyperlink
     * @param state     the current formatting state to be applied to the hyperlink
     * @return the position in the text after the processed hyperlink or the next position
     * to be evaluated if no valid hyperlink is found
     */
    @ServiceOperation("MarkdownTextService.processLink")
    private int processLink(XWPFParagraph paragraph, String text, int startPos, FormatState state) {
        int closingBracket = findClosingBracket(text, startPos);
        if (closingBracket == -1) return startPos + 1;

        if (closingBracket + 1 < text.length() && text.charAt(closingBracket + 1) == '(') {
            int closingParen = findClosingParen(text, closingBracket + 1);
            if (closingParen != -1) {
                String linkText = text.substring(startPos + 1, closingBracket);
                String url = text.substring(closingBracket + 2, closingParen);
                addHyperlink(paragraph, linkText, url, state);
                return closingParen + 1;
            }
        }
        return startPos + 1;
    }

    /**
     * Finds the closing position of a specified marker within a given text, starting from a specific position.
     * This method is recursive if the marker is a single character and appears consecutively.
     *
     * @param text     The text in which to search for the marker.
     * @param startPos The position in the text to start the search from.
     * @param marker   The marker to search for in the text.
     * @return The position of the closing marker in the text, or -1 if the marker is not found.
     */
    @ServiceOperation("MarkdownTextService.findClosingMarker")
    private int findClosingMarker(String text, int startPos, String marker) {
        int pos = text.indexOf(marker, startPos);
        if (pos == -1) return -1;

        if (marker.length() == 1 && pos + 1 < text.length() && text.charAt(pos + 1) == marker.charAt(0)) {
            return findClosingMarker(text, pos + 2, marker);
        }
        return pos;
    }

    /**
     * Finds the position of the closing bracket ']' that matches the opening bracket '['
     * starting from a given position in the provided string.
     *
     * @param text     the input string containing the brackets to be matched
     * @param startPos the position of the opening bracket '[' in the string from where the search starts
     * @return the index of the matching closing bracket ']', or -1 if no matching closing bracket is found
     */
    @ServiceOperation("MarkdownTextService.findClosingBracket")
    private int findClosingBracket(String text, int startPos) {
        int level = 1;
        for (int i = startPos + 1; i < text.length(); i++) {
            if (text.charAt(i) == '[') level++;
            else if (text.charAt(i) == ']') {
                level--;
                if (level == 0) return i;
            }
        }
        return -1;
    }

    /**
     * Finds the position of the closing parenthesis that matches the opening parenthesis
     * at the specified starting position in the given text.
     *
     * @param text     the input string containing parentheses
     * @param startPos the position of the opening parenthesis for which the matching closing parenthesis is to be found
     * @return the index of the matching closing parenthesis if found, otherwise -1
     */
    @ServiceOperation("MarkdownTextService.findClosingParen")
    private int findClosingParen(String text, int startPos) {
        int level = 1;
        for (int i = startPos + 1; i < text.length(); i++) {
            if (text.charAt(i) == '(') level++;
            else if (text.charAt(i) == ')') {
                level--;
                if (level == 0) return i;
            }
        }
        return -1;
    }

    /**
     * Adds a formatted text run to the given paragraph with styling based on the specified format state.
     *
     * @param paragraph the XWPFParagraph to which the text run will be added
     * @param text      the text content to be added to the paragraph
     * @param state     the formatting state specifying the styles to apply, such as bold, italic, and strikethrough
     */
    @ServiceOperation("MarkdownTextService.addFormattedRun")
    private void addFormattedRun(XWPFParagraph paragraph, String text, FormatState state) {
        if (text.isEmpty()) return;

        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(state.bold);
        run.setItalic(state.italic);
        run.setStrike(state.strike);
    }

    /**
     * Adds a hyperlink to the specified paragraph with the provided text and URL,
     * applying formatting based on the given state. In case of an error, the hyperlink
     * is added as a non-clickable styled text.
     *
     * @param paragraph the paragraph to which the hyperlink will be added
     * @param text      the display text of the hyperlink
     * @param url       the URL that the hyperlink will point to
     * @param state     the formatting state to apply (e.g., bold, italic, strike-through)
     */
    @ServiceOperation("MarkdownTextService.addHyperlink")
    private void addHyperlink(XWPFParagraph paragraph, String text, String url, FormatState state) {
        try {
            String relationshipId = paragraph.getDocument().getPackagePart()
                    .addExternalRelationship(url, XWPFRelation.HYPERLINK.getRelation())
                    .getId();

            CTHyperlink hyperlink = paragraph.getCTP().addNewHyperlink();
            hyperlink.setId(relationshipId);

            XWPFHyperlinkRun run = new XWPFHyperlinkRun(hyperlink, hyperlink.addNewR(), paragraph);
            run.setText(text);
            run.setUnderline(UnderlinePatterns.SINGLE);
            run.setColor("0000FF");
            run.setBold(state.bold);
            run.setItalic(state.italic);
            run.setStrike(state.strike);
        } catch (Exception e) {
            XWPFRun run = paragraph.createRun();
            run.setText(text);
            run.setUnderline(UnderlinePatterns.SINGLE);
            run.setColor("0000FF");
            run.setBold(state.bold);
            run.setItalic(state.italic);
            run.setStrike(state.strike);
        }
    }
}