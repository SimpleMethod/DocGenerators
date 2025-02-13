package pl.mlodawski.docgenerator.utils.generatearchive;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.stereotype.Controller;
import pl.mlodawski.docgenerator.pluginsystem.core.registry.ServiceOperation;
import pl.mlodawski.docgenerator.utils.generatearchive.exception.PackageZipArchiveException;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
@Author Michał Młodawski
*/

/**
 * Service responsible for generating ZIP archives from provided file data.
 * Implements the {@link IArchiveGenerationController} interface and defines the
 * functionality required for archive generation.
 *
 * This service uses the `ZipArchiveOutputStream` to create a ZIP file dynamically
 * in memory based on the contents of the provided file map.
 *
 * Key Responsibilities:
 * - Processing a map of file names and their respective data as byte arrays.
 * - Generating a ZIP archive in byte array format from the input map.
 * - Handling errors and wrapping them in a custom exception {@link PackageZipArchiveException}.
 *
 * Annotations:
 * - `@Controller`: Indicates that this service is a Spring-managed component.
 * - `@Slf4j`: Provides logging capabilities for the service.
 *
 * Exception Handling:
 * - Throws a {@link PackageZipArchiveException} when any issue related to the ZIP
 *   creation process arises.
 */
@Controller
@Slf4j
public class ArchiveGenerationService implements IArchiveGenerationController {


    /**
     * Generates a ZIP archive containing the files provided in the input map.
     * Each entry in the map represents a file, where the key is the file name
     * and the value is the file content as a byte array.
     *
     * @param fileMap a TreeMap where the keys are file names and the values
     *                are file contents in byte array format.
     * @return a byte array representing the generated ZIP archive.
     * @throws PackageZipArchiveException if there is an error during the ZIP
     *                                    archive generation process.
     */
    @ServiceOperation("ArchiveGenerationService.generateZipArchive")
    public byte[] generateZipArchive(final TreeMap<String, byte[]> fileMap) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ZipArchiveOutputStream zos = new ZipArchiveOutputStream(bos);
            for (final Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
                final ZipArchiveEntry entry1 = new ZipArchiveEntry(entry.getKey());
                entry1.setSize(entry.getValue().length);
                zos.putArchiveEntry(entry1);
                zos.write(entry.getValue());
                zos.closeArchiveEntry();
            }
            zos.finish();
            zos.close();
            bos.close();
            return bos.toByteArray();
        } catch (final Exception e) {
            ArchiveGenerationService.log.error("Problem with generate a ZIP archive:" + e.getMessage());
            throw new PackageZipArchiveException("Problem with generate a ZIP archive", e.getCause());
        }
    }

}
