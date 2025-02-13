package pl.mlodawski.docgenerator.docmanager.searchtext;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import pl.mlodawski.docgenerator.docmanager.searchtext.exception.MissingRequiredValueException;
import pl.mlodawski.docgenerator.docmanager.searchtext.exception.ValueNotFoundException;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;

import java.util.List;
import java.util.Optional;

/*
 @Author Michał Młodawski
 */
/**
 * Service for searching and extracting text or paragraphs from DOCX documents.
 * Provides utility methods for locating text in paragraphs, tables, and entire documents.
 */
@Service
@Slf4j
public class SearchTextService implements IDocumentSearchOperations {

    /**
     * Finds a paragraph in the text
     *
     * @param document   DOCX document in which to search for the searched value
     * @param searchWord Search value
     * @return Number of the paragraphs with the searched value
     */
    @ServiceOperation("SearchTextService.searchParagraph")
    public Integer searchParagraph(XWPFDocument document, final String searchWord) {
        Integer searchIndex = 0;
        final Optional<String> optTextToSearch = Optional.ofNullable(searchWord);
        final List<XWPFParagraph> paragraphList = document.getParagraphs();
        if (optTextToSearch.isEmpty()) {
            log.warn("Search text is required");
            throw new MissingRequiredValueException("Search text is required");
        }
        for (final XWPFParagraph paragraph : paragraphList) {
            if (paragraph.getText().equals(optTextToSearch.get())) {
                return searchIndex;
            }
            searchIndex++;
        }
        log.warn("The requested text was not found: {}", searchWord);
        throw new ValueNotFoundException("The requested text was not found: " + searchWord);
    }

    /**
     * Searches for a row with a searched text
     *
     * @param table      Table number to be searched
     * @param searchWord Requested text to be search
     * @return Number of the paragraphs with the searched value
     */
    @ServiceOperation("SearchTextService.searchRowNumberInTableByText")
    public Integer searchRowNumberInTableByText(final XWPFTable table, final String searchWord) {
        Integer rowsSize = table.getRows().size();
        final Optional<String> optTextToSearch = Optional.ofNullable(searchWord);
        if (optTextToSearch.isEmpty()) {
            log.error("The text field is mandatory");
            throw new MissingRequiredValueException("The text field is mandatory");
        }
        for (Integer i = 0; i < rowsSize; i++) {
            if (table.getRow(i).getCell(0).getText().equals(optTextToSearch.get())) {
                return i;
            }
        }
        log.warn("The requested text was not found: {}", searchWord);
        throw new ValueNotFoundException("The requested text was not found: " + searchWord);
    }

    /**
     * Searches the entire document including paragraphs and tables for the requested value
     *
     * @param document   DOCX document in which to search for the searched value
     * @param searchWord Search value
     * @return Paragraph as XWPFRun
     */
    @ServiceOperation("SearchTextService.getTextUsingTemplateCode")
    public XWPFRun getTextUsingTemplateCode(final XWPFDocument document, final String searchWord) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (paragraph.getText().equals(searchWord)) {
                clearParagraph(paragraph);
                XWPFRun run = paragraph.createRun();
                return run;
            }
        }

        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        if (paragraph.getText().equals(searchWord)) {
                            clearParagraph(paragraph);
                            XWPFRun run = paragraph.createRun();
                            return run;
                        }
                    }
                }
            }
        }

        log.warn("The requested text was not found: {}", searchWord);
        throw new ValueNotFoundException("The requested text was not found: " + searchWord);
    }



    /**
     * Clears all runs from the specified XWPFParagraph, effectively removing
     * all text and formatting from the paragraph.
     *
     * @param paragraph the XWPFParagraph object to be cleared of all runs
     */
    private void clearParagraph(XWPFParagraph paragraph) {
        int size = paragraph.getRuns().size();
        for (int i = size - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
    }

    /**
     * Searches the entire document including paragraphs and tables for the requested value
     *
     * @param document   DOCX document in which to search for the searched value
     * @param searchWord Search value
     * @return Paragraph as XWPFParagraph
     */
    @ServiceOperation("SearchTextService.getTextUsingTemplateCodeAndReturnParagraphs")
    public XWPFParagraph getTextUsingTemplateCodeAndReturnParagraphs(final XWPFDocument document, String searchWord) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (paragraph.getText().equals(searchWord)) {
                clearParagraph(paragraph);
                paragraph.createRun();
                return paragraph;
            }
        }
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        if (paragraph.getText().equals(searchWord)) {
                            clearParagraph(paragraph);
                            paragraph.createRun();
                            return paragraph;
                        }
                    }
                }
            }
        }

        log.warn("The requested text was not found: {}", searchWord);
        throw new ValueNotFoundException("The requested text was not found: " + searchWord);
    }


    /**
     * Return paragraph based on row, cell and paragraph number
     *
     * @param table           Table
     * @param rowNumber       Row number
     * @param cellNumber      Cell number
     * @param paragraphNumber Paragraph number
     * @return Paragraph handle
     */
    @ServiceOperation("SearchTextService.getParagraph")
    public XWPFParagraph getParagraph(final XWPFTable table, final Integer rowNumber, final Integer cellNumber, final Integer paragraphNumber) {
        try {
            XWPFTableRow row = table.getRow(rowNumber);
            if (row != null) {
                XWPFTableCell cell = row.getCell(cellNumber);
                if (cell != null) {
                    XWPFParagraph paragraph = cell.getParagraphs().get(paragraphNumber);
                    if (paragraph != null) {
                        return paragraph;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Cannot find a paragraph in the specified table: {}", e.getMessage());
            throw new ValueNotFoundException("Cannot find a paragraph in the specified table: " + e.getMessage());
        }
        log.error("Cannot find a paragraph in the specified table");
        throw new ValueNotFoundException("Cannot find a paragraph in the specified table");
    }


}
