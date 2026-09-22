package com.site.Cloudstore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;

@Setter
@Getter
@Entity
@Table(name = "files")
public class CloudstoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name",unique = true, nullable = false)
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_data", columnDefinition = "BYTEA")
    private byte[] fileData;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;


    public CloudstoreEntity() {
    }

    public CloudstoreEntity(String fileName, String fileType, byte[] fileData,
                            Long fileSize, LocalDateTime uploadDate, String description) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
    }

    // toString (optional, for debugging)
    @Override
    public String toString() {
        return "CloudstoreEntity{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", uploadDate=" + uploadDate +
                '}';
    }
}