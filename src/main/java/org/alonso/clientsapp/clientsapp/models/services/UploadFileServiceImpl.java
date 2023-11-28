package org.alonso.clientsapp.clientsapp.models.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements UploadFileService {

    private final Logger logger = LoggerFactory.getLogger(UploadFileServiceImpl.class);
    private static final String UPLOAD_DIR = "uploads/";

    @Override
    public Resource load(String filename, String folder) throws MalformedURLException {
        Path routeFile = this.getPath(filename, folder);
        logger.info(routeFile.toString());
        Resource resource = new UrlResource(routeFile.toUri());

        if (!resource.exists() && !resource.isReadable()) {
            routeFile = Paths.get(UPLOAD_DIR + folder).resolve("no-image.png").toAbsolutePath();
            resource = new UrlResource(routeFile.toUri());
        }

        return resource;
    }

    @Override
    public String copy(MultipartFile file, String folder) throws IOException {
        String filename = UUID.randomUUID().toString() + file.getOriginalFilename();
        Path path = this.getPath(filename, folder);
        logger.info(path.toString());

        Files.copy(file.getInputStream(), path);
        return filename;
    }

    @Override
    public boolean delete(String filename, String folder) {
        if (filename != null && filename.length() > 0) {
            Path routeImage = Paths.get(UPLOAD_DIR + folder).resolve(filename).toAbsolutePath();
            File prevFile = routeImage.toFile();

            if (prevFile.exists() && prevFile.canRead()) {
                prevFile.delete();
                return true;
            }
        }
        return false;
    }

    @Override
    public Path getPath(String filename, String folder) {
        return Paths.get(UPLOAD_DIR + folder).resolve(filename).toAbsolutePath();
    }

}
