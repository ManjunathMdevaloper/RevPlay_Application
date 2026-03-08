package com.revature.revplay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class serves as a configuration component for the Web MVC layer of the
 * application.
 * It implements the WebMvcConfigurer interface, which allows us to customize
 * how Spring
 * handles web requests, specifically regarding how static resources and file
 * uploads are served.
 * By using the @Configuration annotation, we tell Spring that this class
 * contains bean
 * definitions and configuration settings that should be processed during the
 * application startup.
 * The primary purpose here is to bridge the gap between files stored on the
 * local hard drive
 * and the web-accessible URLs used by the browser to display images or play
 * music.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * This method is an override from the WebMvcConfigurer interface and is
     * automatically
     * called by Spring during initialization to setup resource handling logic.
     * 
     * Inside this method, we perform the following actions:
     * 1. We receive a ResourceHandlerRegistry object which is the system's map for
     * static files.
     * 2. We call our helper method 'exposeDirectory' to specifically target the
     * "uploads" folder.
     * 3. This ensures that any file stored in the physical 'uploads' directory
     * becomes
     * reachable through a specific URL pattern in the web browser.
     * 4. It acts as the gatekeeper that tells the web server where to look for
     * user-generated content.
     * 5. This is essential for features like profile pictures, album covers, and
     * audio files.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Line below: Trigger the exposure of the 'uploads' directory to the web
        // registry
        exposeDirectory("uploads", registry);
    }

    /**
     * This private helper method contains the core logic for mapping a physical
     * folder
     * on the computer's disk to a virtual path that the web server can understand.
     * 
     * The logic follows these detailed steps:
     * 1. It takes the name of the directory (like "uploads") and the registry as
     * inputs.
     * 2. It converts the relative folder name into a 'Path' object for standardized
     * file handling.
     * 3. It calculates the absolute path (the full address on the hard drive) of
     * that folder.
     * 4. It performs a safety check to clean up the directory name if it contains
     * relative movements.
     * 5. It registers a 'ResourceHandler' which defines the URL prefix (e.g.,
     * /uploads/**).
     * 6. It links that URL prefix to the physical 'file:/' location on the disk.
     * 7. Finally, it enables the browser to request
     * 'http://domain/uploads/photo.jpg' and
     * automatically fetch the literal file from the local storage.
     */
    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        // Line below: Create a Path object from the provided directory name string
        Path uploadDir = Paths.get(dirName);

        // Line below: Resolve the full, absolute physical path of the folder on the
        // system
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Line below: Check if the directory name starts with parent directory
        // navigation
        if (dirName.startsWith("../")) {
            // Line below: Remove the parent navigators to normalize the path for the URL
            dirName = dirName.replace("../", "");
        }

        // Line below: Map the web URL pattern to the physical directory location on the
        // disk
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}