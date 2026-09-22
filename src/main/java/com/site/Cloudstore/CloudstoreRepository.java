package com.site.Cloudstore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CloudstoreRepository extends JpaRepository<CloudstoreEntity, Long> {

    //GETCURRENTSIZE
    @Query("SELECT SUM(f.fileSize) FROM CloudstoreEntity f")
    Long getCurrentSize();

    //DOWNLOAD
    @Query("SELECT f FROM CloudstoreEntity f WHERE f.fileName = :fileName ")
    CloudstoreEntity findFile(@Param("fileName") String fileName);

    //RENAME
    @Modifying
    @Transactional
    @Query("UPDATE CloudstoreEntity f SET f.fileName = :fileName WHERE f.id = :id")
    Long renameFile(@Param("id") Long id, @Param("fileName") String fileName);

    //DELETE
    @Modifying
    @Transactional
    @Query("DELETE FROM CloudstoreEntity f WHERE f.fileName = :fileName")
    void deleteByFileName(@Param("fileName") String fileName);

    //FIND FILE NAME SIZE DATE
    @Query("SELECT f.fileName, f.fileSize, f.uploadDate FROM CloudstoreEntity f")
    List<Object[]> findFileNameSizeDate();

    //FINDALL
    List<CloudstoreEntity> findAll();

    CloudstoreEntity getById(Long id);
    boolean existsByFileName(String fileName);
    boolean findByFileName(String fileName);

    boolean existsById(Long id);
}