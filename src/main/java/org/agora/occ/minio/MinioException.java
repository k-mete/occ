package org.agora.occ.minio;

/**
 * Runtime exception thrown when a MinIO operation fails.
 */
public class MinioException extends RuntimeException {

    /**
     * Creates a new MinioException with a detail message.
     *
     * @param message the detail message
     */
    public MinioException(String message) {
        super(message);
    }

    /**
     * Creates a new MinioException with a detail message and cause.
     *
     * @param message the detail message
     * @param cause   the root cause
     */
    public MinioException(String message, Throwable cause) {
        super(message, cause);
    }
}
