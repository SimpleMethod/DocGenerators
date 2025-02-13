package pl.mlodawski.docgenerator.docmanager.searchtext;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

/*
 @Author Michał Młodawski
 */
/**
 * Interface for document search operations in DOCX documents.
 * Provides methods to search and retrieve text or paragraphs from
 * various parts of a DOCX document, including paragraphs and tables.
 */
public interface IDocumentSearchOperations {
    /**
     * Finds a paragraph in the text.
     *
     * @param document   DOCX document in which to search for the searched value
     * @param searchWord Search value
     * @return Number of the paragraphs with the searched value
     */
    Integer searchParagraph(XWPFDocument document, String searchWord);

    /**
     * Searches for a row with a searched text.
     *
     * @param table      Table number to be searched
     * @param searchWord Requested text to be searched
     * @return Number of the row with the searched value
     */
    Integer searchRowNumberInTableByText(XWPFTable table, String searchWord);

    /**
     * Searches the entire document including paragraphs and tables for the requested value.
     *
     * @param document   DOCX document in which to search for the searched value
     * @param searchWord Search value
     * @return Paragraph as XWPFRun
     */
    XWPFRun getTextUsingTemplateCode(XWPFDocument document, String searchWord);

    /**
     * Searches the entire document including paragraphs and tables for the requested value.
     *
     * @param document   DOCX document in which to search for the searched value
     * @param searchWord Search value
     * @return Paragraph as XWPFParagraph
     */
    XWPFParagraph getTextUsingTemplateCodeAndReturnParagraphs(XWPFDocument document, String searchWord);

    /**
     * Returns a paragraph based on row, cell, and paragraph number.
     *
     * @param table           Table
     * @param rowNumber       Row number
     * @param cellNumber      Cell number
     * @param paragraphNumber Paragraph number
     * @return Paragraph handle
     */
    XWPFParagraph getParagraph(XWPFTable table, Integer rowNumber, Integer cellNumber, Integer paragraphNumber);
}


