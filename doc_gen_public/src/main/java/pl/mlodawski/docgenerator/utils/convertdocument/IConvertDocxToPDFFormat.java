package pl.mlodawski.docgenerator.utils.convertdocument;

/*
 @Author Michał Młodawski
 */
public interface IConvertDocxToPDFFormat {

    byte[] convertDocxToPDF(byte[] wordDocument);
}
