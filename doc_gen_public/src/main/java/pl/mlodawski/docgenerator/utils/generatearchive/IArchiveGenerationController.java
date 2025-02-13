package pl.mlodawski.docgenerator.utils.generatearchive;

import java.util.TreeMap;

/*
 @Author Michał Młodawski
 */
public interface IArchiveGenerationController {

    byte[] generateZipArchive(TreeMap<String, byte[]> fileMap);

}
