package pl.mlodawski.docgenerator.app;



import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 @Author Michał Młodawski
 */
@SpringBootApplication
@ConfigurationPropertiesScan("pl.mlodawski.docgenerator.config")
@ComponentScan(basePackages = {
        "pl.mlodawski.docgenerator",
})
@EnableScheduling
public class ModuleCardGeneratorApplication implements ApplicationRunner {

    public static void main(final String[] args) {
        SpringApplication.run(ModuleCardGeneratorApplication.class, args);
    }

    @Override
    public void run(final ApplicationArguments args){
    }
}
