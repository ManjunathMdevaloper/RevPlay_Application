package com.revature.revplay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class provides additional Web MVC configuration specifically for
 * handling static resources.
 * Like WebConfig, it implements WebMvcConfigurer to tap into Spring's resource
 * handling lifecycle.
 * The primary focus of this specific file is to ensure that the "uploads"
 * directory, which
 * contains user-uploaded media like audio files and images, is correctly mapped
 * to a web URL.
 * This is crucial because Spring, by default, only serves files from the
 * 'static' or 'public'
 * folders inside the project. For external folders on the absolute file system,
 * we must
 * explicitly tell the server how to find and serve them.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * This method overrides the default resource handler setup to add custom
     * directory mappings.
     * 
     * The process followed in this method includes:
     * 1. Identifying the "uploads" folder in the root of the project.
     * 2. Finding exactly where that folder lives on the physical computer (Absolute
     * Path).
     * 3. Normalizing the path to ensure it ends with a forward slash for URL
     * compatibility.
     * 4. Creating a public web route ("/uploads/**") that links to that physical
     * location.
     * 5. This allows the application to serve dynamic content without needing a
     * separate web server.
     * 6. It ensures that media stays persistent even when the application is
     * recompiled or restarted.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Line below: Create a Path object representing the local 'uploads' directory
        Path uploadDir = Paths.get("uploads");

        // Line below: Convert that local path into a full computer-readable absolute
        // address
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Line below: Check if the generated path string is missing the terminal slash
        if (!uploadPath.endsWith("/")) {
            // Line below: Append a forward slash to make the path a proper directory
            // reference
            uploadPath += "/";
        }

        // Line below: Register the web URL pattern '/uploads/**' to map directly to the
        // physical files
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath);
    }
}
