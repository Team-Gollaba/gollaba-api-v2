package org.tg.gollaba.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.s3")
public record S3Locations (
    Directory pollItems,
    Directory profileImages,
    Directory backgroundImages
) {
    public record Directory(
        String location
    ) {
    }
}
