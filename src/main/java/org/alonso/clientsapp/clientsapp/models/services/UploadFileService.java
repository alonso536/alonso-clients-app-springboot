package org.alonso.clientsapp.clientsapp.models.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface UploadFileService {
    Resource load(String filename, String folder) throws MalformedURLException;

    String copy(MultipartFile file, String folder) throws IOException;

    boolean delete(String filename, String folder);

    Path getPath(String filename, String folder);
}
