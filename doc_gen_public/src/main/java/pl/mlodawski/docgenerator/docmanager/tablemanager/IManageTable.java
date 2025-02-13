package pl.mlodawski.docgenerator.docmanager.tablemanager;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/*
 @Author Michał Młodawski
 */
/**
 * Interface for managing operations on tables in Word documents.
 * Provides methods to perform operations such as merging cells horizontally or vertically,
 * inserting new rows, and committing table rows to the underlying XML structure.
 */

public interface IManageTable {

    /**
     * Merges cells in a specified row of a Word table horizontally within the given column range.
     *
     * @param table   The table in which cells are to be merged.
     * @param row     The row number where the cells will be merged.
     * @param fromCol The starting column index for the merge.
     * @param toCol   The ending column index for the merge.
     */
    void mergeCellHorizontally(XWPFTable table, Integer row, Integer fromCol, Integer toCol);

    /**
     * Inserts a new table row into the specified position of a table.
     *
     * @param sourceTableRow The source table row to copy for creating the new row.
     * @param pos            The position index where the new row is to be inserted (0-based).
     * @return The newly inserted table row.
     */
    XWPFTableRow insertNewTableRow(XWPFTableRow sourceTableRow, Integer pos);

    /**
     * Commits the current state of the table rows to the underlying XML structure.
     * This method ensures that the rows in the Word document table are properly
     * synchronized with their corresponding XML representation.
     *
     * @param table The table whose rows are to be committed to the underlying XML structure.
     */
    void commitTableRows(XWPFTable table);

    /**
     * Merges cells vertically in the specified table from the first row to the last row in the given column.
     * The contents of cells in subsequent rows will be cleared, and their associated paragraphs will be replaced.
     *
     * @param table       The table where the vertical merge operation will be performed.
     * @param initialCell The column index of the cells to be merged.
     * @param firstRow    The starting row index of the vertical merge.
     * @param lastRow     The ending row index of the vertical merge.
     */
    void mergeCellVertically(XWPFTable table, Integer initialCell, Integer firstRow, Integer lastRow);
}
