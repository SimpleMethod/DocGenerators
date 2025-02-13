package pl.mlodawski.docgenerator.docmanager.listgenerator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import pl.mlodawski.docgenerator.docmanager.listgenerator.exception.ListCreatorException;
import pl.mlodawski.docgenerator.docmanager.richtext.MarkdownTextService;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;


import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

 /*
 @Author Michał Młodawski
 */

/**
 * This service provides functionalities to generate and manipulate lists within
 * DOCX documents, specifically handling paragraphs and tables. It relies on
 * MarkdownTextService to process the content and ensures proper formatting of
 * text according to the needs of the document structure.
 */
@Service
@Slf4j
@AllArgsConstructor
public class ListGeneratorService implements ITextListGenerator {

    private MarkdownTextService markdownTextService;

    /**
     * Adds a list of paragraphs from the specified paragraph into the table
     *
     * @param table       Table to which the list should be added
     * @param listContent List to add into the table
     * @param rowNumber   Row to which the list should be added
     * @Author Michał Młodawski
     */
    @ServiceOperation("ListGeneratorService.insertListContentInTable")
    public void insertListContentInTable(final XWPFTable table, final List<String> listContent, final Integer rowNumber, final Integer cellNumber, final ParagraphAlignment... paragraphAlignment) {
        for (int i = 0; i < listContent.size() - 1; i++) {
            table.getRow(rowNumber).getCell(cellNumber).addParagraph();
        }
        try {
            Integer paragraphIndex = 0;
            Integer key = 1;
            for (final String s : listContent) {
                Optional<XWPFParagraph> optionalXWPFParagraph = Optional.ofNullable(table.getRows().get(rowNumber).getCell(cellNumber).getParagraphs().get(paragraphIndex));
                if (optionalXWPFParagraph.isPresent()) {
                    if (paragraphAlignment.length > 0) {
                        markdownTextService.processMarkdown(optionalXWPFParagraph.get(), s);
                        optionalXWPFParagraph.get().setAlignment(paragraphAlignment[0]);
                    } else {
                        markdownTextService.processMarkdown(optionalXWPFParagraph.get(), key + ".  " + s);
                    }
                }
                paragraphIndex++;
                key++;
            }
        } catch (final Exception e) {
            ListGeneratorService.log.error("Problem with removing or inserting data into a table: " + e.getMessage());
            throw new ListCreatorException("Problem with removing or inserting data into a table: " + e.getMessage(), e.getCause());
        }
    }

    /**
     * Adds a list of paragraphs from the specified paragraph in the document
     *
     * @param document            DOCX document
     * @param listContent         List to add to the document
     * @param startParagraphIndex Initial paragraph number
     * @return XWPF document
     * @Author Michał Młodawski
     */
    @ServiceOperation("ListGeneratorService.insertListContent")
    public XWPFDocument insertListContent(final XWPFDocument document, final List<String> listContent, final Integer startParagraphIndex) {
        try {
            int posOfParagraph = document.getPosOfParagraph(document.getParagraphArray(startParagraphIndex));

            while (posOfParagraph < document.getBodyElements().size() - 1
                    && document.getBodyElements().get(posOfParagraph + 1) instanceof XWPFParagraph) {
                document.removeBodyElement(posOfParagraph + 1);
            }

            document.removeBodyElement(posOfParagraph);

            for (String content : listContent) {
                XWPFParagraph paragraph = document.createParagraph();
                paragraph.setNumID(BigInteger.valueOf(1));
                markdownTextService.processMarkdown(paragraph, content);
            }

        } catch (final ArrayIndexOutOfBoundsException e) {
            log.error("Problem with removing or inserting data into a table: " + e.getMessage());
            throw new ListCreatorException("Problem with removing or inserting data into a table: " + e.getMessage(), e.getCause());
        }
        return document;
    }

    /**
     * Inserts the content of a list into a given paragraph. Clears all existing runs
     * in the paragraph before adding the new content. Each element of the list
     * is processed and added to the paragraph, with line breaks inserted between
     * elements.
     *
     * @param paragraph   the XWPFParagraph object into which the list content will be inserted
     * @param listContent the list of strings to be added into the paragraph
     */
    @ServiceOperation("ListGeneratorService.insertListContentIntoParagraph")
    public void insertListContentIntoParagraph(final XWPFParagraph paragraph, final List<String> listContent) {
        try {
            while (!paragraph.getRuns().isEmpty()) {
                paragraph.removeRun(0);
            }
            for (String content : listContent) {
                markdownTextService.processMarkdown(paragraph, content);

                if (!content.equals(listContent.get(listContent.size() - 1))) {
                    XWPFRun newLineRun = paragraph.createRun();
                    newLineRun.addBreak();
                }
            }
        } catch (final Exception e) {
            log.error("Problem with modifying the paragraph: " + e.getMessage());
            throw new ListCreatorException("Problem with modifying the paragraph: " + e.getMessage(), e.getCause());
        }
    }
}

