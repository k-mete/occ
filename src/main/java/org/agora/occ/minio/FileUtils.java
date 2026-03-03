package org.agora.occ.minio;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Utility helpers for MinIO file naming and uploads.
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Generates a unique object name with a prefix and inferred file extension.
     *
     * @param prefix string prefix (e.g. "uuid-0-")
     * @param image  the multipart file upload
     * @return a unique object name string
     */
    public static String generateImageObjectName(String prefix, FileUpload image) {
        return prefix + UUID.randomUUID() + "." + getFileExtension(image.fileName());
    }

    /**
     * Uploads a single {@link FileUpload} to MinIO using the given object name.
     *
     * @param minioService the MinIO service to delegate to
     * @param objectName   the destination object key in MinIO
     * @param image        the multipart file to upload
     * @return the public URL of the uploaded object
     * @throws IOException if the file cannot be read
     */
    public static String uploadFile(MinioService minioService, String objectName, FileUpload image) throws IOException {
        try (InputStream inputStream = Files.newInputStream(image.uploadedFile())) {
            return minioService.uploadFile(
                    objectName,
                    inputStream,
                    image.size(),
                    image.contentType());
        }
    }

    /**
     * Extracts the file extension from a filename.
     *
     * @param fileName the filename (e.g. "photo.jpg")
     * @return the extension without the leading dot, or an empty string
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains("."))
            return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
