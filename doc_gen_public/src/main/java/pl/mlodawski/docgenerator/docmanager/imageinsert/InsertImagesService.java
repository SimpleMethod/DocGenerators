package pl.mlodawski.docgenerator.docmanager.imageinsert;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import pl.mlodawski.docgenerator.docmanager.imageinsert.exception.ImageException;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 @Author Michał Młodawski
 */

/**
 * Service responsible for inserting images into various sections of a Microsoft Word document using Apache POI.
 * This class provides functionality to add images into document headers, footers, and specific paragraphs.
 * Implements the {@code IInsertImages} interface.
 */
@Service
@Slf4j
public class InsertImagesService implements IInsertImages {


    /**
     * Adds image to document header
     *
     * @param document       document to insert images
     * @param imageOfFaculty image files to insert into document
     * @return XWPF document
     * @Author Michał Młodawski
     */
    @ServiceOperation("InsertImagesService.insertImagesIntoHeader")
    public XWPFDocument insertImagesIntoHeader(final XWPFDocument document, byte[] imageOfFaculty, int width, int height) {
        InputStream targetStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        targetStream = new ByteArrayInputStream(imageOfFaculty);

        try {
            final XWPFHeader header = document.createHeader(HeaderFooterType.FIRST);
            XWPFParagraph paragraph = document.createParagraph();
            paragraph = header.getParagraphArray(0);
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            final XWPFRun run = paragraph.createRun();

            run.addPicture(targetStream, Document.PICTURE_TYPE_PNG, "filename", Units.toEMU(width), Units.toEMU(height));
            targetStream.close();
        } catch (final Exception e) {
            InsertImagesService.log.error("Problem with inserting image into document header: " + e.getMessage());
            throw new ImageException("Problem with inserting image into document header: " + e.getMessage(), e.getCause());

        }
        return document;
    }

    /**
     * Adds image to document footer
     *
     * @param document       document to insert images
     * @param imageOfFaculty image files to insert into document
     * @param width          image width
     * @param height         image height
     * @return XWPF document
     */
    @ServiceOperation("InsertImagesService.insertImagesIntoFooter")
    public XWPFDocument insertImagesIntoFooter(final XWPFDocument document, byte[] imageOfFaculty, int width, int height) {
        InputStream targetStream = new ByteArrayInputStream(imageOfFaculty);
        try {
            final XWPFFooter footer = document.createFooter(HeaderFooterType.FIRST);
            XWPFParagraph paragraph = footer.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            final XWPFRun run = paragraph.createRun();
            run.addPicture(targetStream, Document.PICTURE_TYPE_PNG, "filename", Units.toEMU(width), Units.toEMU(height));

            targetStream.close();
        } catch (final Exception e) {
            InsertImagesService.log.error("Problem with inserting image into document footer: " + e.getMessage());
            throw new ImageException("Problem with inserting image into document footer: " + e.getMessage(), e.getCause());
        }

        return document;
    }

    /**
     * Adds image to document paragraph
     * @param paragraph paragraph to insert image
     * @param imageBytes image files to insert into document
     * @param width image width
     * @param height image height
     */
    @ServiceOperation("InsertImagesService.insertImageIntoParagraph")
    public XWPFRun insertImageIntoParagraph(XWPFParagraph paragraph, byte[] imageBytes, int width, int height) {
        try (InputStream targetStream = new ByteArrayInputStream(imageBytes)) {

            XWPFRun run = paragraph.createRun();
            run.addPicture(targetStream, Document.PICTURE_TYPE_PNG, "filename", Units.toEMU(width), Units.toEMU(height));
            return run;
        } catch (Exception e) {
            throw new ImageException("Problem with inserting image into paragraph: " + e.getMessage(), e);
        }
    }
}
