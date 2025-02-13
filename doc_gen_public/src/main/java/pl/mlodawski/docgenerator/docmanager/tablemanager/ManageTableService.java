package pl.mlodawski.docgenerator.docmanager.tablemanager;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.stereotype.Service;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;

import java.math.BigInteger;

/*
 @Author Michał Młodawski
 */

/**
 * Service class responsible for performing operations on tables in Word documents.
 * Provides functionalities for managing table cell merges (both horizontally and vertically),
 * adding new rows, and committing table rows to the underlying XML structure.
 * Implements the {@link IManageTable} interface.
 */

@Service
@Slf4j
public class ManageTableService implements IManageTable {


    /**
     * Merges table cells in a horizontal way
     *
     * @param table      Table for manage
     * @param initialRow Rows to manage
     * @param firstCell  First cell to merge
     * @param lastCell   Last cell to merge
     */
    @ServiceOperation("ManageTableService.mergeCellHorizontally")
    public void mergeCellHorizontally(XWPFTable table, final Integer initialRow, final Integer firstCell, final Integer lastCell) {
        XWPFTableCell cell = table.getRow(initialRow).getCell(firstCell);
        CTTcPr tcPr = cell.getCTTc().getTcPr();
        if (tcPr == null) tcPr = cell.getCTTc().addNewTcPr();
        if (tcPr.isSetGridSpan()) {
            tcPr.getGridSpan().setVal(BigInteger.valueOf(lastCell - firstCell + 1));
        } else {
            tcPr.addNewGridSpan().setVal(BigInteger.valueOf(lastCell - firstCell + 1));
        }
        for (int colIndex = lastCell; colIndex > firstCell; colIndex--) {
            table.getRow(initialRow).removeCell(colIndex);
        }
    }

    /**
     * Adds a new row to the table
     *
     * @param sourceTableRow Table for insert a new rows
     * @param pos            Position of rows to insert
     * @return Handle for new rows
     */
    @SneakyThrows
    @ServiceOperation("ManageTableService.insertNewTableRow")
    public XWPFTableRow insertNewTableRow(XWPFTableRow sourceTableRow, final Integer pos) {
        XWPFTable table = sourceTableRow.getTable();
        CTRow newCTRrow = CTRow.Factory.parse(sourceTableRow.getCtRow().newInputStream());
        XWPFTableRow tableRow = new XWPFTableRow(newCTRrow, table);
        table.addRow(tableRow, pos);
        return tableRow;
    }

    /**
     * Transcribes the XML tab code
     *
     * @param table Table for transcribe
     */
    @ServiceOperation("ManageTableService.commitTableRows")
    public void commitTableRows(XWPFTable table) {
        int rowNr = 0;
        for (XWPFTableRow tableRow : table.getRows()) {
            table.getCTTbl().setTrArray(rowNr++, tableRow.getCtRow());
        }
    }


    /**
     * Merges table rows in a vertically
     *
     * @param table       Table for manage
     * @param initialCell Cell to manage
     * @param firstRow    First row to merge
     * @param lastRow     Last row to merge
     */
    @ServiceOperation("ManageTableService.mergeCellVertically")
    public void mergeCellVertically(XWPFTable table, final Integer initialCell, final Integer firstRow, final Integer lastRow) {
        for (int rowIndex = firstRow; rowIndex <= lastRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(initialCell);
            CTVMerge vmerge = CTVMerge.Factory.newInstance();
            if (rowIndex == firstRow) {
                vmerge.setVal(STMerge.RESTART);
            } else {
                vmerge.setVal(STMerge.CONTINUE);
                for (int i = cell.getParagraphs().size(); i > 0; i--) {
                    cell.removeParagraph(0);
                }
                cell.addParagraph();
            }
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr == null) tcPr = cell.getCTTc().addNewTcPr();
            tcPr.setVMerge(vmerge);
        }
    }
}
