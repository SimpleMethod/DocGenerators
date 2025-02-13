package pl.mlodawski.docgenerator.docmanager.pageanalyzer;

import org.apache.poi.xwpf.usermodel.XWPFDocument;


/**
 * Interface defining methods for analyzing and optimizing page breaks in a Word document.
 */
public interface IPageBreakAnalyzer {

    /**
     * Optimizes page breaks in the given Word document.
     * Identifies and removes unnecessary page breaks that disrupt content flow and tables.
     *
     * @param document The XWPFDocument object representing the Word document.
     */
    void optimizePageBreaks(XWPFDocument document);


}
