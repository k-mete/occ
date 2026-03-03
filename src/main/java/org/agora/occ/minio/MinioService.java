package org.agora.occ.minio;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;

/**
 * Service layer over the MinIO client.
 * Provides upload, download, and deletion operations.
 */
@ApplicationScoped
public class MinioService {

    @ConfigProperty(name = "app.minio.bucket")
    String bucket;

    private final MinioClient minioClient;

    @Inject
    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * Returns the configured bucket name.
     *
     * @return the bucket name
     */
    public String getBucketName() {
        return bucket;
    }

    /**
     * Uploads a file to MinIO and returns the clean object URL (no query params).
     *
     * @param objectName  the destination key within the bucket
     * @param inputStream the file data
     * @param size        the byte size of the file
     * @param contentType the MIME type of the file
     * @return the clean public URL of the uploaded object
     * @throws MinioException if the bucket does not exist or upload fails
     */
    public String uploadFile(String objectName, InputStream inputStream, long size, String contentType) {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!bucketExists) {
                throw new MinioException(String.format(MinioConstant.ERROR_BUCKET_NOT_FOUND, bucket));
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType != null ? contentType : "application/octet-stream")
                            .build());
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectName)
                            .build());
            return getCleanObjectUrl(presignedUrl);
        } catch (MinioException e) {
            throw e;
        } catch (Exception e) {
            throw new MinioException(String.format(MinioConstant.ERROR_FILE_UPLOAD, objectName), e);
        }
    }

    /**
     * Downloads a file from MinIO and returns an {@link InputStream}.
     *
     * @param objectName the object key within the bucket
     * @return the file data as an input stream
     * @throws MinioException if the download fails
     */
    public InputStream getObject(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new MinioException("Failed to get object: " + objectName, e);
        }
    }

    /**
     * Deletes a file from MinIO.
     *
     * @param objectName the object key to remove
     * @throws MinioException if the deletion fails
     */
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new MinioException(String.format(MinioConstant.ERROR_FILE_DELETE, objectName), e);
        }
    }

    /**
     * Strips query parameters from a presigned URL to obtain the clean object URL.
     *
     * @param presignedUrl the presigned URL
     * @return the URL without query parameters
     */
    public String getCleanObjectUrl(String presignedUrl) {
        if (presignedUrl == null)
            return null;
        return presignedUrl.contains("?")
                ? presignedUrl.substring(0, presignedUrl.indexOf("?"))
                : presignedUrl;
    }
}
