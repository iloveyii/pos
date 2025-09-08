package net.softhem.pos.service;

import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path imagesLocation;
    private final Path targetImagesLocation;
    private final Path targetPdfFilesLocation;
    private final Path targetTexFilesLocation;
    // private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public FileStorageService() throws IOException {
        // Development: save to target/classes/static/images/products
        this.targetImagesLocation = Paths.get("target/classes/static/images/products")
                .toAbsolutePath().normalize();

        // Create path to resources/static/images/products
        this.imagesLocation = Paths.get("src/main/resources/static/images/products")
                .toAbsolutePath().normalize();
        this.targetPdfFilesLocation = Paths.get("src/main/resources/static/pdf")
                .toAbsolutePath().normalize();
        String texPath = "/data/tex";
        if(!Files.exists(Path.of(texPath))) {
            texPath = "src/main/resources/static/tex";
        }
        this.targetTexFilesLocation = Paths.get(texPath)
                .toAbsolutePath().normalize();

        // Create both directories
        Files.createDirectories(targetImagesLocation);
        Files.createDirectories(imagesLocation);
        Files.createDirectories(targetPdfFilesLocation);
        Files.createDirectories(targetTexFilesLocation);
    }

    public String storeBase64Image(String base64Data) throws IOException {
        if (base64Data == null || base64Data.isEmpty()) {
            return null;
        }

        try {
            // Extract the base64 part from the data URL
            String[] parts = base64Data.split(",");
            String base64String = parts.length > 1 ? parts[1] : parts[0];

            // Extract file extension from MIME type
            String mimeType = parts[0].split(";")[0].split(":")[1];
            String extension = getExtensionFromMimeType(mimeType);

            // Generate unique filename
            String filename = "product_" + UUID.randomUUID().toString() + "." + extension;
            // Save to both locations for development and production
            saveToFileLocation(imagesLocation, filename, base64String);
            saveToFileLocation(targetImagesLocation, filename, base64String);
            return filename;
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid base64 data", e);
        }
    }

    private void saveToFileLocation(Path filLocationToSave, String filename, String base64String) throws IOException {
        Path destinationFile = filLocationToSave.resolve(filename).normalize();

        // Security check
        if (!destinationFile.getParent().equals(filLocationToSave)) {
            throw new IOException("Cannot store file outside current directory");
        }

        // Decode and save the file
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        Files.write(destinationFile, imageBytes);
    }

    public Resource loadImageAsResource(String filename) throws IOException {
        try {
            Path filePath = imagesLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("File not found or not readable: " + filename);
            }
        } catch (Exception e) {
            throw new IOException("Could not load file: " + filename, e);
        }
    }

    public boolean deleteImage(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            return false;
        }

        Path filePath = imagesLocation.resolve(filename).normalize();

        // Security check
        if (!filePath.getParent().equals(imagesLocation)) {
            throw new IOException("Cannot delete file outside current directory");
        }

        return Files.deleteIfExists(filePath);
    }

    private String getExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "image/jpeg": return "jpg";
            case "image/png": return "png";
            case "image/gif": return "gif";
            case "image/webp": return "webp";
            case "image/svg+xml": return "svg";
            default: return "png"; // default to png
        }
    }

    /**
     * Write StringBuilder to file using Files.write()
     */
    public void writeLatexStringToFile(String filename, String content) throws IOException {
        Path destinationFile = targetTexFilesLocation.resolve(filename).normalize();
        Files.createDirectories(Path.of("/data/pdf/" + filename.replace(".tex", "")));

        Files.writeString(destinationFile, content);
        // logger.info("StringBuilder content written to: {}", destinationFile);
    }

}