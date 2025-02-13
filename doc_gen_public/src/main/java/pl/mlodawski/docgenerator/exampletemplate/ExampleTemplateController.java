package pl.mlodawski.docgenerator.exampletemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mlodawski.api.core.PluginStatus;
import pl.mlodawski.docgenerator.pluginsystem.core.manager.PluginManager;
import pl.mlodawski.docgenerator.utils.filemanager.TemplateResource;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class ExampleTemplateController {

    private final PluginManager pluginManager;

    @TemplateResource(path = "example-template.docx", module = "example-template")
    private byte[] templateDocument;

    @TemplateResource(path = "images/example_1.png", module = "example-template")
    private byte[] exampleImage;


    /**
     * Generates a DOCX document containing example content by utilizing a plugin system to process a template and include specific resources.
     *
     * @return A ResponseEntity object containing the generated DOCX document as a byte array and associated HTTP headers.
     */
    @Operation(
            summary = "Generate example document",
            description = "Generates a DOCX document with example content"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully generated DOCX document",
                    content = @Content(
                            mediaType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during document generation"
            )
    })
    @PostMapping(
            value = "example-template/docx",
            produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )
    @ResponseBody
    @SneakyThrows
    public ResponseEntity<byte[]> generateExampleDocument() {

        final HttpHeaders headers = new HttpHeaders();

        pluginManager.initializePlugin("example-plugin");
        pluginManager.enablePlugin("example-plugin");

        PluginStatus status = pluginManager.getPluginStatus("example-plugin");
        log.info("Plugin status: {}", status);

        pluginManager.getPluginMethods("example-plugin").forEach(log::info);

        Map<String, Object> setTemplateParams = new HashMap<>();
        setTemplateParams.put("templateDocument", templateDocument);
        pluginManager.invokePluginMethod("example-plugin", "setTemplateDocument", setTemplateParams);

        Map<String, Object> setImageParams = new HashMap<>();
        setImageParams.put("exampleImage", exampleImage);
        pluginManager.invokePluginMethod("example-plugin", "setExampleImage", setImageParams);

        Map<String, Object> processParams = new HashMap<>();
        byte[] result = (byte[]) pluginManager.invokePluginMethod("example-plugin", "processTemplate", processParams);

        return ResponseEntity.ok()
                .headers(headers)
                .body(result);

    }
}
