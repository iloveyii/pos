package net.softhem.pos.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    public String storeBase64Image(String base64Data) throws IOException {
        if (base64Data == null || base64Data.isEmpty()) {
            return null;
        }

        // Extract the base64 part from the data URL
        String[] parts = base64Data.split(",");
        String base64String = parts.length > 1 ? parts[1] : parts[0];

        // Extract file extension from MIME type
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String extension = getExtensionFromMimeType(mimeType);

        // Generate unique filename
        String filename = UUID.randomUUID().toString() + "." + extension;
        Path destinationFile = rootLocation.resolve(Paths.get(filename)).normalize().toAbsolutePath();

        // Decode and save the file
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        Files.write(destinationFile, imageBytes);

        return filename;
    }

    private String getExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "image/jpeg": return "jpg";
            case "image/png": return "png";
            case "image/gif": return "gif";
            case "image/webp": return "webp";
            default: return "bin";
        }
    }
}