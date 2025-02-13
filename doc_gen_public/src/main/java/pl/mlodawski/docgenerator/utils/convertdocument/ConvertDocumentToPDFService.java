package pl.mlodawski.docgenerator.utils.convertdocument;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pl.mlodawski.docgenerator.config.app.AppConfig;
import pl.mlodawski.docgenerator.utils.convertdocument.exception.NetworkConnectionException;

/*
 @Author Michał Młodawski
 */
@Slf4j
@Service
@AllArgsConstructor
public class ConvertDocumentToPDFService implements IConvertDocxToPDFFormat {

    private final RestTemplate restTemplate;
    private final AppConfig appConfig;


    /**
     * Returns a PDF document based on a DOCX document. Use an external service to convert the document.
     *
     * @param wordDocument Bit array with a text document in a DOCX format
     * @return Bit array with a text document in a PDF format
     */
    public byte[] convertDocxToPDF(final byte[] wordDocument) {
        try {
            ByteArrayResource fileResource = new ByteArrayResource(wordDocument) {
                @Override
                public String getFilename() {
                    return "file.docx";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file.docx", fileResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    appConfig.getDocxToPdfConverterUrl() + "/forms/libreoffice/convert",
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );

            log.info("POST status: {}", response.getStatusCode().is2xxSuccessful());

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Problem with connection to the PDF converter service: HTTP Code: {}",
                        response.getStatusCode().value());
                throw new NetworkConnectionException(
                        "Problem with connection to the PDF converter service: HTTP Code: " +
                                response.getStatusCode().value(),
                        null
                );
            }

            return response.getBody();
        } catch (Exception e) {
            log.error("Problem with processing PDF file: {}", e.getMessage());
            throw new NetworkConnectionException("Problem with processing PDF file: " + e.getMessage(),
                    e.getCause());
        }
    }
}

