package pl.mlodawski.docgenerator.docmanager.imageinsert;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/*
 @Author Michał Młodawski
 */
/**
 * IInsertImages is an interface defining methods for inserting images into different parts of a Word document.
 * This includes functionality to add images into headers, footers, and specific paragraphs using Apache POI library.
 *
 * Responsibilities:
 * - Abstracts the insertion of images into various document sections.
 * - Supports specifying the size of the images (width and height).
 * - Provides methods to handle header, footer, and paragraph-level image insertion.
 *
 * All implementing classes must provide their own specific logic for making use of these methods.
 */
public interface IInsertImages {

    /**
     * Inserts an image into the header of a Word document.
     *
     * @param document       the document to insert the image into
     * @param imageBytes     the image data as a byte array
     * @param width          the width of the image
     * @param height         the height of the image
     * @return               the modified XWPFDocument instance
     */
    XWPFDocument insertImagesIntoHeader(XWPFDocument document, byte[] imageBytes, int width, int height);

    /**
     * Inserts an image into the footer of a Word document.
     *
     * @param document       the document to insert the image into
     * @param imageBytes     the image data as a byte array
     * @param width          the width of the image
     * @param height         the height of the image
     * @return               the modified XWPFDocument instance
     */
    XWPFDocument insertImagesIntoFooter(XWPFDocument document, byte[] imageBytes, int width, int height);

    /**
     * Inserts an image into a specific paragraph of a Word document.
     *
     * @param paragraph      the paragraph to insert the image into
     * @param imageBytes     the image data as a byte array
     * @param width          the width of the image
     * @param height         the height of the image
     */
    XWPFRun insertImageIntoParagraph(XWPFParagraph paragraph, byte[] imageBytes, int width, int height);
}
