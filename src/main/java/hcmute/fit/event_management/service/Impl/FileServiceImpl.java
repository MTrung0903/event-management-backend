package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.service.IFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
@Service
public class FileServiceImpl implements IFileService {

    @Value("${upload.path}")
    private String uploadPath;
    private Path path;


    @Override
    public Resource load(String fileName) {
        try {
            // Gọi init() để đảm bảo path được khởi tạo trước khi sử dụng
            if (path == null) {
                init();
            }

            Path file = path.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error load file " + e.getMessage());
            return null;
        }
    }



    @Override
    public boolean saveFiles(MultipartFile file) {
        try{
            //System.out.println("kiemtra path " + path);
            init();
            Files.copy(file.getInputStream(),path.resolve(file.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);

            return true;
        }catch (Exception e){
            System.out.println("Error save file " + e.getMessage());
            return  false;
        }
    }
    private void init() {
        try {
            path = Paths.get(uploadPath);
            System.out.println("Upload path: " + path);  // Log để kiểm tra giá trị path
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            System.out.println("Error create root folder " + e.getMessage());
        }
    }


}
