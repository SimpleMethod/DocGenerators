package pl.mlodawski.docgenerator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/*
 @Author Michał Młodawski
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Generate module cards for Kielce University of Technology",
                version = "v1",
                description = "Core functionality to generate documents in PDF, DOCX and ZIP archive formats",
                contact = @Contact(
                        name = "Michał Młodawski",
                        email = "michal@mlodaw.ski",
                        url = "https://mlodaw.ski"
                ),
                license = @License(
                        name = "GNU AGPLv3",
                        url = "https://www.gnu.org/licenses/agpl-3.0.html"
                )
        ),
        servers = {
                @Server(url = "https://modulecard.oroshi.tu.kielce.pl", description = "Production Server"),
                @Server(url = "https://modulecard.oroshi.app", description = "Staging Server"),
                @Server(url = "http://localhost:8081", description = "Local Development Server")
        }
)
public class OpenApi30Config {
}
