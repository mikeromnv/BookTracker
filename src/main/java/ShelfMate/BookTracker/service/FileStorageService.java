package ShelfMate.BookTracker.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {


    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String fileExtension = file.getOriginalFilename();
        String newFilename = UUID.randomUUID() + fileExtension ;
        Path uploadDir = Paths.get("src/main/resources/static/images/books");

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination = uploadDir.resolve(newFilename);
        file.transferTo(destination);

        return newFilename;
    }
}