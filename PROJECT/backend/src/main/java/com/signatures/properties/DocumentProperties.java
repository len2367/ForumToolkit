package com.signatures.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "documents")
public class DocumentProperties {
    private String path;

    public void setPath(String path) {
        if (path.startsWith("/")) {
            this.path = path.endsWith("/") ? path : path + "/";
        } else {
            String workingPath = Paths.get("")
                    .toAbsolutePath()
                    .toString();

            this.path = workingPath + "/" + (path.endsWith("/") ? path : path + "/");
        }
    }
}
