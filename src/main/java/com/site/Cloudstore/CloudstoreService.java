package com.site.Cloudstore;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@Getter
@Setter
@EnableAsync
public class CloudstoreService {

    @Value("${storage.storageSize}")
    private long sizeOfStorage;

    private final CloudstoreRepository repository;
    private static final Logger log = LoggerFactory.getLogger(CloudstoreService.class);
    public CloudstoreService(CloudstoreRepository repository) {
        this.repository = repository;
    }


    public CloudstoreEntity register(String user) {
        log.info("Called method register for user {}", user);
        CloudstoreEntity entity = new CloudstoreEntity();
        entity.setFileName(user);
        return repository.save(entity);
    }

    //Upload
    public CloudstoreEntity uploadFile(MultipartFile file) throws Exception {
        log.info("Called method uploadFile: {}", file.getOriginalFilename());

        if (getSizeOfStorage() > 100) {
            throw new Exception("Limit storage");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("No file name");
        }
        originalName = java.nio.file.Paths.get(originalName).getFileName().toString();

        String base = originalName;
        String ext = "";
        int dot = originalName.lastIndexOf('.');
        if (dot > 0) {
            base = originalName.substring(0, dot);
            ext  = originalName.substring(dot);
        }

        String finalName = originalName;
        int counter = 1;
        while (repository.existsByFileName(finalName)) {
            finalName = base + " (" + counter + ")" + ext;
            counter++;
        }

        CloudstoreEntity entity = new CloudstoreEntity();
        entity.setFileName(finalName);
        entity.setFileType(file.getContentType());
        entity.setFileData(file.getBytes());
        entity.setFileSize(file.getSize());
        entity.setUploadDate(LocalDateTime.now());
        return repository.save(entity);
    }

    public List<CloudstoreEntity> uploadFiles(List<MultipartFile> files) {
        log.info("Called method uploadFiles:");
        return files.stream()
                .map(file -> {
                    try {
                        return  uploadFile(file);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                })
                .toList();
    }

    //Download
    public CloudstoreEntity downloadFileByName(String fileName) {
        log.info("Called method getFileByName");
        return repository.findFile(fileName);
    }

    public CloudstoreEntity downloadFilesByName(List<String> FilesToDownload) throws IOException {
        log.info("Called method getFileByName");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)){
            for(String filename : FilesToDownload){
                CloudstoreEntity fileToDownload = downloadFileByName(filename);

                ZipEntry entry = new ZipEntry(fileToDownload.getFileName());
                zos.putNextEntry(entry);
                zos.write(fileToDownload.getFileData());
                zos.closeEntry();
            }

        byte[] zipBytes = new ZipEntry(Arrays.toString(baos.toByteArray())).getExtra();
        return null;
        }
    }


    // Rename
    public String renameFile(long id, String name) {
        log.info("Called method renameFile: {} by id: {}", name, id);

        CloudstoreEntity entity = repository.getById(id);

        if (!repository.existsById(id)) {
            return "File with id " + id + " not found";
        }

        if (name.equals(entity.getFileName())) {
            return "File " + name + " already has this name";
        }

        Long rowsAffected = repository.renameFile(id, name);
        if (rowsAffected == 0) {
            throw new RuntimeException("File with id " + id + " not found for rename");
        }
        return "File " + name + " successfully renamed";
    }


    // Delete
    public void deleteFileByName(String fileName) {
        log.info("Deleting file with name: {}", fileName);

        repository.deleteByFileName(fileName);
    }


    public List<CloudstoreEntity> getAllFiles() {
        log.info("Called method all files");
        return repository.findAll();
    }

    public List<Object[]> getAllFileNameAndSizeAndType() {
        log.info("Called method all files Name");
        return repository.findFileNameSizeDate();
    }


    public long getSizeOfStorage(){
        long currentSize = Optional.ofNullable(repository.getCurrentSize()).orElse(0L);

        if (currentSize == 0){
            return 0;
        }else{
            return ((currentSize/1000)/sizeOfStorage);
        }
    }


}