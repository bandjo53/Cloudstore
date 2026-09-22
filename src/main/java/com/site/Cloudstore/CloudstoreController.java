package com.site.Cloudstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api")
public class CloudstoreController {

    private final Logger log = LoggerFactory.getLogger(CloudstoreController.class);
    private final CloudstoreService cloudstoreService;

    public CloudstoreController(CloudstoreService reservationService) {
        this.cloudstoreService = reservationService;
    }


    @PostMapping("/test/{testNameUser}")
    public ResponseEntity<String> test(
            @PathVariable String testNameUser
    ){
        cloudstoreService.register(testNameUser);
        return ResponseEntity.status(200)
                .body("ok");
    }

    @GetMapping("/All")
    public ResponseEntity<List<Object[]>> getAll()
    {
        List<Object[]> all = cloudstoreService.getAllFileNameAndSizeAndType();
        return ResponseEntity.status(200)
                .body(all);
    }

    @GetMapping("/SizeOfStorage")
    public ResponseEntity<String> getSize(){
        long size = cloudstoreService.getSizeOfStorage();
        return ResponseEntity.status(200)
                .body("size is " + size);
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile fileToUpload) throws Exception
    {
        cloudstoreService.uploadFile(fileToUpload);
        return ResponseEntity.status(200).
                body("Upload successfully");
    }
    @PostMapping("/uploads")
    public ResponseEntity<String> uploadFiles(
            @RequestParam("file") List<MultipartFile> filesToUpload) throws Exception
    {
        cloudstoreService.uploadFiles(filesToUpload);
        return ResponseEntity.status(200).
                body("Upload successfully");
    }


    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(
            @RequestParam("file") String NameToDownload)
    {
        CloudstoreEntity fileToDownload = cloudstoreService.downloadFileByName(NameToDownload);

        byte[] data = fileToDownload.getFileData();
        String contentType = fileToDownload.getFileType();
        String originalName = fileToDownload.getFileName();

        return ResponseEntity.status(200)
                .contentType(MediaType.parseMediaType(contentType))
                .body("Download successfully " + NameToDownload);
    }
    @GetMapping("/downloads")
    public ResponseEntity<byte[]> downloadFiles(
            @RequestParam("files") List<String> FilesToDownload) throws IOException
    {
        byte[] zipBytes = cloudstoreService.downloadFilesByName(FilesToDownload).getFileData();
        return ResponseEntity.status(200)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }


    @PatchMapping("/rename")
    public ResponseEntity<String> renameFile(
            @RequestParam("id") int IdToRename,
            @RequestParam("fileName") String NameToRename
            )
    {
        String result = cloudstoreService.renameFile(IdToRename, NameToRename);
        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile (
            @RequestParam("fileName") String fileToDelete)
    {
            cloudstoreService.deleteFileByName(fileToDelete);
            return ResponseEntity.status(200)
                    .body("Delete successfully " + fileToDelete);
        }
}