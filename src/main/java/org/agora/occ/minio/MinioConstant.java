package org.agora.occ.minio;

/**
 * Error message constants for MinIO operations.
 */
public final class MinioConstant {

    private MinioConstant() {
    }

    /** Thrown when the configured bucket does not exist. */
    public static final String ERROR_BUCKET_NOT_FOUND = "Bucket not found: %s";

    /** Thrown when a file upload fails. */
    public static final String ERROR_FILE_UPLOAD = "Failed to upload file: %s";

    /** Thrown when a file deletion fails. */
    public static final String ERROR_FILE_DELETE = "Failed to delete file: %s";
}
