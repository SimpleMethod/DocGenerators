package pl.mlodawski.docgenerator.docmanager.listgenerator;


import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import java.util.List;

/*
 @Author Michał Młodawski
 */
/**
 * Interface defining methods for generating and manipulating lists within DOCX documents.
 */
public interface ITextListGenerator {

    /**
     * Inserts a list of paragraphs into a specified table cell.
     *
     * @param table             Table to which the list should be added.
     * @param listContent       List to add into the table.
     * @param rowNumber         Row in which the list should be added.
     * @param cellNumber        Cell in which the list should be added.
     * @param paragraphAlignment (Optional) Alignment for the paragraph content.
     */
    void insertListContentInTable(XWPFTable table, List<String> listContent, Integer rowNumber, Integer cellNumber, ParagraphAlignment... paragraphAlignment);

    /**
     * Inserts a list of paragraphs into a DOCX document.
     *
     * @param document            DOCX document where the list should be inserted.
     * @param listContent         List to add to the document.
     * @param startParagraphIndex Initial paragraph index for inserting content.
     * @return Modified XWPFDocument with inserted list content.
     */
    XWPFDocument insertListContent(XWPFDocument document, List<String> listContent, Integer startParagraphIndex);

    /**
     * Inserts list content into an existing paragraph.
     *
     * @param paragraph   XWPFParagraph where the list content should be inserted.
     * @param listContent List of strings to add into the paragraph.
     */
    void insertListContentIntoParagraph(XWPFParagraph paragraph, List<String> listContent);
}