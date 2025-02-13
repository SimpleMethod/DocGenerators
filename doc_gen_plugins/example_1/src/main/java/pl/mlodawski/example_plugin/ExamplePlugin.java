package pl.mlodawski.example_plugin;

import lombok.extern.slf4j.Slf4j;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import pl.mlodawski.api.operation.AbstractPlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
@Author Michał Młodawski
*/
/**
 * Represents a plugin that provides functionality for processing template
 * documents with predefined text, images, and current dates. This plugin
 * extends the AbstractPlugin class and integrates with the application using
 * a unique ID, name, and version.
 */
@Slf4j
public class ExamplePlugin extends AbstractPlugin {
    /**
     * The unique identifier for the Example Plugin.
     * This ID is used to register and distinguish the plugin
     * within the middleware application.
     */
    private static final String ID = "example-plugin";
    /**
     * A constant representing the name of the plugin.
     * This name is used as a human-readable identifier for the plugin in various contexts.
     */
    private static final String NAME = "Example Plugin";
    /**
     * Represents the version of the Example Plugin.
     * The version is used to identify the current release of the plugin
     * and ensures compatibility with*/
    private static final String VERSION = "1.0.1";

    /**
     * A byte array used to store the content of a template document.
     * This document serves as the base template for processing operations,
     * such as inserting predefined text, images, and dates into specific placeholders.
     */
    private byte[] templateDocument;
    /**
     * A byte array representing the example image used during the processing of template documents.
     * This image is inserted into a specific placeholder within the template document, if defined.
     * The image must be set before invoking any operations that require the use of this example image.
     */
    private byte[] exampleImage;

    /**
     * Retrieves the unique identifier for the plugin.
     *
     * @return a {@code String} representing the unique ID of the plugin.
     */
    @Override
    public String getId() {
        return ID;
    }

    /**
     * Returns the name of the plugin.
     *
     * @return the name of the plugin as a String
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Retrieves the version of the plugin.
     *
     * @return a string representing the version of the plugin.
     */
    @Override
    public String getVersion() {
        return VERSION;
    }

    /**
     * Sets the template document content as a byte array.
     * This document will be used as the base template for processing operations.
     *
     * @param templateDocument the byte array representing the content of the template document
     */
    public void setTemplateDocument(byte[] templateDocument) {
        this.templateDocument = templateDocument;
    }

    /**
     * Sets the example image to be used in the document processing.
     *
     * @param exampleImage a byte array containing the image data to be inserted
     *                     into the template document at the designated placeholder.
     */
    public void setExampleImage(byte[] exampleImage) {
        this.exampleImage = exampleImage;
    }

    /**
     * Processes the provided template document by inserting predefined text, an example image, and the
     * current date into specific placeholders in the document. The modified document is then
     * converted to a byte array and returned. If the template document is not set, an exception is
     * thrown. Logs information and errors throughout the operation.
     *
     * @return a byte array representing the processed Word document with the inserted content.
     * @throws IllegalStateException if no template document is set before processing.
     * @throws RuntimeException if an error occurs while processing the template.
     */
    public byte[] processTemplate() {
        if (templateDocument == null) {
            context.logError("Template document not set", new IllegalStateException());
            throw new IllegalStateException("Template document not set");
        }

        try {
            final InputStream targetStream = new ByteArrayInputStream(templateDocument);
            XWPFDocument document = new XWPFDocument(targetStream);
            targetStream.close();
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            insertPredefinedText(document);
            insertExampleImage(document);
            insertCurrentDate(document);

            document.write(outputStream);
            document.close();
            outputStream.close();

            context.log("Template processed successfully");
            return outputStream.toByteArray();

        } catch (Exception e) {
            context.logError("Error processing template", e);
            throw new RuntimeException("Error processing template", e);
        }
    }

    /**
     * Inserts predefined text into a Word document at a specific placeholder.
     * The text is processed from markdown format and includes styled and rich
     * text content. If the placeholder is found in the document, the text is
     * retrieved and added to the designated area.
     *
     * @param document the Word document (XWPFDocument) where the predefined text
     *                 should be inserted into a designated placeholder.
     * @throws RuntimeException if an error occurs during the text insertion process.
     */
    private void insertPredefinedText(XWPFDocument document) {
        try {
            XWPFParagraph paragraph = context.executeOperation(
                    "SearchTextService.getTextUsingTemplateCodeAndReturnParagraphs",
                    document,
                    "cd-example-text-1"
            );

            if (paragraph != null) {
                String markdownText = "~~Document creator~~ allows you to generate *files* in this system, **which means all your files** are [automatically](https://mlodaw.ski) saved locally and accessible **offline!**";

                context.executeOperation(
                        "MarkdownTextService.processMarkdown",
                        paragraph,
                        markdownText
                );
            }
        } catch (Exception e) {
            context.logError("Error inserting predefined text", e);
            throw new RuntimeException("Error inserting predefined text", e);
        }
    }

    /**
     * Inserts an example image into a specified placeholder within a Word document
     * if the placeholder and the image are available. The inserted image is sized
     * to 400x400 pixels.
     *
     * @param document the Word document (XWPFDocument) where the example image
     *                 should be inserted into a designated placeholder.
     * @throws RuntimeException if an error occurs during the image insertion process.
     */
    private void insertExampleImage(XWPFDocument document) {
        if (exampleImage == null) {
            context.log("Example image not set, skipping image insertion");
            return;
        }

        try {
            XWPFParagraph paragraph = context.executeOperation(
                    "SearchTextService.getTextUsingTemplateCodeAndReturnParagraphs",
                    document,
                    "cd-example-image-1"
            );

            if (paragraph != null) {
                context.executeOperation(
                        "InsertImagesService.insertImageIntoParagraph",
                        paragraph,
                        exampleImage,
                        400,
                        400
                );
            }
        } catch (Exception e) {
            context.logError("Error inserting image", e);
            throw new RuntimeException("Error inserting image", e);
        }
    }

    /**
     * Inserts the current date and time into a specific placeholder in a given Word document
     * if the placeholder is found. The date is formatted as "yyyy-MM-dd HH:mm:ss" and additional
     * styling (e.g., font size) is applied to the inserted text.
     *
     * @param document the Word document (XWPFDocument) where the current date and time should be inserted
     *                 into a designated placeholder.
     * @throws RuntimeException if an error occurs while inserting the date and time.
     */
    private void insertCurrentDate(XWPFDocument document) {
        try {
            XWPFRun dateField = context.executeOperation(
                    "SearchTextService.getTextUsingTemplateCode",
                    document,
                    "cd-example-text-2"
            );

            if (dateField != null) {
                String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                context.executeOperation("ParagraphBuilderService.setStyleEntryPoint", dateField);
                context.executeOperation("ParagraphBuilderService.setFontSize", dateField, 12);
                context.executeOperation("ParagraphBuilderService.setText", dateField, currentDate);
            }
        } catch (Exception e) {
            context.logError("Error inserting current date", e);
            throw new RuntimeException("Error inserting current date", e);
        }
    }
}