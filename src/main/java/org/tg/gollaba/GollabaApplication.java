package org.tg.gollaba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GollabaApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            GollabaApplication.class,
            args
        );
    }

}
