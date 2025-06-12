package hcmute.fit.event_management.controller.admin;

import hcmute.fit.event_management.service.IFileService;
import hcmute.fit.event_management.service.Impl.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private IFileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String publicId = cloudinaryService.uploadFile(file);
        return ResponseEntity.ok(publicId);
    }

    @GetMapping("/download/{publicId}")
    public ResponseEntity<String> getFileUrl(@PathVariable String publicId) {
        String fileUrl = cloudinaryService.getFileUrl(publicId);
        return ResponseEntity.ok(fileUrl);
    }

    @DeleteMapping("/delete/{publicId}")
    public ResponseEntity<String> deleteFile(@PathVariable String publicId) throws IOException {
        boolean deleted = cloudinaryService.deleteFile(publicId);
        if (deleted) {
            return ResponseEntity.ok("File deleted: " + publicId);
        } else {
            return ResponseEntity.status(404).body("File not found: " + publicId);
        }
    }
    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<Resource> viewImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            System.out.println("<<<<<<<<<" + resource + ">>>>>>>>>>>>>");
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Xác định kiểu file (jpg, png, ...)
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}