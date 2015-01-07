package de.zalando.platform.awsutilizationmonitor;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Main {
    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .showBanner(false)
                .sources(Main.class)
                .run(args);
    }
}
