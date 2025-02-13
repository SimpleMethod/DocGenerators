package pl.mlodawski.docgenerator.docmanager.pageanalyzer;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;

import java.util.List;

/*
 @Author Michał Młodawski
 */

@Service
@Slf4j
public class PageBreakAnalyzerService implements IPageBreakAnalyzer {
    //TODO: Verify if the page break works correctly

    /**
     * Optimizes page breaks in a given Word document by identifying and removing unnecessary
     * page breaks that disrupt the flow of tables and content.
     *
     * @param document the XWPFDocument instance representing the Word document to be processed
     */
    @ServiceOperation("PageBreakAnalyzerService.optimizePageBreaks")
    public void optimizePageBreaks(XWPFDocument document) {
        try {
            List<IBodyElement> elements = document.getBodyElements();
            XWPFTable currentTable = null;

            for (int i = 0; i < elements.size(); i++) {
                IBodyElement element = elements.get(i);

                if (element instanceof XWPFTable) {
                    currentTable = (XWPFTable) element;
                    log.debug("Found table at position {}", i);
                }
                else if (element instanceof XWPFParagraph) {
                    XWPFParagraph paragraph = (XWPFParagraph) element;

                    if (hasPageBreak(paragraph)) {
                        if (isBreakingTable(elements, i)) {
                            removePageBreak(paragraph);
                            log.debug("Removed page break at position {}", i);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error optimizing page breaks: " + e.getMessage(), e);
        }
    }

    /**
     * Determines whether a specific page break in a Word document disrupts a table's flow by
     * analyzing the elements following the page break for certain conditions.
     *
     * @param elements the list of IBodyElement instances representing the document's body elements
     * @param breakPosition the index of the page break in the elements list
     * @return true if the page break affects a table by breaking its content or introducing unwanted elements, false otherwise
     */
    private boolean isBreakingTable(List<IBodyElement> elements, int breakPosition) {
        int elementCount = 0;
        boolean foundContent = false;

        for (int i = breakPosition + 1; i < elements.size(); i++) {
            IBodyElement element = elements.get(i);

            if (element instanceof XWPFParagraph) {
                XWPFParagraph para = (XWPFParagraph) element;
                String text = para.getText().trim();

                if (!text.isEmpty() && !text.startsWith("**[")) {
                    elementCount++;
                    foundContent = true;
                }

                if (text.startsWith("**[")) {
                    break;
                }
            }
            else if (element instanceof XWPFTable) {
                break;
            }
        }

        log.debug("Found {} elements after break at position {}", elementCount, breakPosition);
        return foundContent && elementCount == 1;
    }

    /**
     * Checks if the given paragraph contains a page break.
     * A page break is identified either as a section property on the paragraph
     * or as a specific type of break element within the paragraph runs.
     *
     * @param paragraph the XWPFParagraph instance to check for the presence of a page break
     * @return true if the paragraph contains a page break; false otherwise
     */
    private boolean hasPageBreak(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        if (ctp.getPPr() != null && ctp.getPPr().getSectPr() != null) {
            return true;
        }

        for (XWPFRun run : paragraph.getRuns()) {
            CTR ctr = run.getCTR();
            if (ctr.getBrList() != null) {
                for (CTBr br : ctr.getBrList()) {
                    if (br.getType() != null && br.getType().equals(STBrType.PAGE)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Removes any page breaks from the given paragraph. This includes both section page breaks
     * and manual page breaks inserted in the paragraph's runs. Additionally, a carriage return
     * is added as a placeholder at the end of the paragraph.
     *
     * @param paragraph the {@link XWPFParagraph} object from which the page breaks need to be removed
     */
    private void removePageBreak(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        if (ctp.getPPr() != null && ctp.getPPr().getSectPr() != null) {
            ctp.getPPr().unsetSectPr();
        }

        for (XWPFRun run : paragraph.getRuns()) {
            CTR ctr = run.getCTR();
            if (ctr.getBrList() != null) {
                for (int i = ctr.getBrList().size() - 1; i >= 0; i--) {
                    CTBr br = ctr.getBrList().get(i);
                    if (br.getType() != null && br.getType().equals(STBrType.PAGE)) {
                        ctr.removeBr(i);
                    }
                }
            }
        }

        XWPFRun newRun = paragraph.createRun();
        newRun.addCarriageReturn();
    }
}