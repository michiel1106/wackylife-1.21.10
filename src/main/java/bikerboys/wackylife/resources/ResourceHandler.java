package bikerboys.wackylife.resources;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ResourceHandler {
    public void copyBundledSingleFile(String resourcePath, Path targetFile) {
        try {
            Files.createDirectories(targetFile.getParent());

            URL resourceUrl = getClass().getResource(resourcePath);

            if (resourceUrl == null) {
                return;
            }

            if (resourceUrl.getProtocol().equals("file")) {
                handleSingleFileNormal(targetFile, resourceUrl);
            }
            else if (resourceUrl.getProtocol().equals("jar")) {
                handleSingleFileJar(targetFile, resourcePath);
            }
            else {
            }
        } catch (Exception e) {

        }
    }

    private void handleSingleFileNormal(Path targetFile, URL resourceUrl) {
        try {
            Path sourcePath = Paths.get(resourceUrl.toURI());

            if (Files.isRegularFile(sourcePath)) {
                Files.copy(sourcePath, targetFile, StandardCopyOption.REPLACE_EXISTING);

            } else {

            }
        } catch (Exception e) {

        }
    }

    private void handleSingleFileJar(Path targetFile, String resourcePath) {
        try {
            try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
                if (in == null) {

                    return;
                }

                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);

            }
        } catch (Exception e) {

        }
    }
}
