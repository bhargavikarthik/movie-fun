package org.superbiz.moviefun.blobstore;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.albums.AlbumsBean;
import sun.nio.ch.IOUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.lang.System.in;
import static java.lang.System.out;
import static java.nio.file.Files.readAllBytes;

public class FileStore implements BlobStore {

    public FileStore() {
    }

    @Override
    public void put(Blob blob) throws IOException {
        saveUploadToFile(IOUtils.toByteArray(blob.inputStream), getCoverFile(Long.parseLong(blob.name)));
    }

    @Override
    public Optional<Blob> get(String name) throws IOException, URISyntaxException {

        Path coverFilePath = getExistingCoverPath(Long.parseLong(name));
        byte[] imageBytes = readAllBytes(coverFilePath);
        return Optional.of(new Blob(name, new ByteArrayInputStream(imageBytes), new Tika().detect(imageBytes)));
    }


    @Override
    public void deleteAll() {
        // ...
    }

    private File getCoverFile(long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }



    private Path getExistingCoverPath(long albumId) {

        Path coverFilePath = null;
    try {
        File coverFile = getCoverFile(albumId);
        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(Thread.currentThread().getContextClassLoader().getResource("default-cover.jpg").toURI());
        }
    }catch (Exception e){
        e.printStackTrace();
    }

        return coverFilePath;
    }

    private void saveUploadToFile(byte[] uploadedFile, File targetFile) throws IOException {
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(uploadedFile);
        }
    }

    private Path getExistingCoverPath(String albumId) throws URISyntaxException {
        File coverFile = new File(albumId);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
        }

        return coverFilePath;
    }



}