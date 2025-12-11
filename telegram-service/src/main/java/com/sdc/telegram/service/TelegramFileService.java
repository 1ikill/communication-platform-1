package com.sdc.telegram.service;

import com.sdc.telegram.config.TelegramClientManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.sdc.telegram.domain.constants.PhotoFileType.PHOTO;
import static com.sdc.telegram.domain.constants.PhotoFileType.PROFILE;

/**
 * Service for handling Telegram file operations including images, videos, and documents
 * @since 12.2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramFileService {
    private final TelegramClientManager clientManager;

    /**
     * Retrieves an image from Telegram chats
     * Gets the RemoteFile by remoteId, downloads it temporarily, and converts it to byte array
     *
     * @param remoteId the Telegram image identifier
     * @param accountId the Telegram account identifier
     * @return byte array of the image
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     * @throws IOException if an I/O error occurs
     */
    public byte[] getTelegramImage(final String remoteId, final String accountId) throws ExecutionException, InterruptedException, IOException {
        final Client client = clientManager.getClient(accountId);
        CompletableFuture<TdApi.File> future = new CompletableFuture<>();
        final TdApi.FileType fileType;
        final String remote;
        if (remoteId.contains(PHOTO.getTitle())){
            fileType = new TdApi.FileTypePhoto();
            remote = remoteId.substring(PHOTO.getTitle().length());
        } else {
            fileType = new TdApi.FileTypeProfilePhoto();
            remote = remoteId.substring(PROFILE.getTitle().length());
        }

        client.send(new TdApi.GetRemoteFile(remote, fileType), result -> {
            if (result instanceof TdApi.File) {
                future.complete((TdApi.File) result);
            } else if (result instanceof TdApi.Error) {
                future.completeExceptionally(new RuntimeException("Error fetching image:" + ((TdApi.Error) result).message));
            } else {
                future.completeExceptionally(new RuntimeException("Failed to fetch image"));
            }
        });

        final Path path = getFilePath(future.get(), client, 10, 300);
        final byte[] imageBytes = Files.readAllBytes(path);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Failed to delete temp file: {}", path, e);
        }

        return imageBytes;
    }

    /**
     * Gets the file path by downloading the file if necessary and waiting for download completion
     *
     * @param file the Telegram file object
     * @param client the Telegram client
     * @param retries the number of retry attempts
     * @param delayMs the delay in milliseconds between retries
     * @return the path to the downloaded file
     * @throws IOException if an I/O error occurs
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     * @throws FileNotFoundException if file is not downloaded after waiting
     */
    public Path getFilePath(TdApi.File file, final Client client, int retries, int delayMs) throws IOException, ExecutionException, InterruptedException {
        if (file.local.path == null || file.local.path.isEmpty() || !Files.exists(Path.of(file.local.path))) {
            CompletableFuture<TdApi.File> downloadFuture = new CompletableFuture<>();

            client.send(new TdApi.DownloadFile(file.id, 1, 0, 0, false), downloadResult -> {
                if (downloadResult instanceof TdApi.File) {
                    downloadFuture.complete((TdApi.File) downloadResult);
                } else {
                    downloadFuture.completeExceptionally(new RuntimeException("Failed to download file"));
                }
            });

            file = downloadFuture.get();
        }

        while ((!file.local.isDownloadingCompleted || file.local.path == null || file.local.path.isEmpty())
                && retries-- > 0) {

            Thread.sleep(delayMs);

            CompletableFuture<TdApi.File> checkFuture = new CompletableFuture<>();
            client.send(new TdApi.GetFile(file.id), result -> {
                if (result instanceof TdApi.File) {
                    checkFuture.complete((TdApi.File) result);
                } else {
                    checkFuture.completeExceptionally(new RuntimeException("Polling failed"));
                }
            });

            file = checkFuture.get();
        }

        if (file.local.path == null || !Files.exists(Path.of(file.local.path))) {
            throw new FileNotFoundException("File still not downloaded after waiting");
        }

        return Path.of(file.local.path);
    }

    /**
     * Retrieves a video from Telegram chats
     * Gets the RemoteFile by remoteId, downloads it temporarily, and converts it to StreamingResponseBody
     *
     * @param remoteId the Telegram video identifier
     * @param accountId the Telegram account identifier
     * @return StreamingResponseBody for the video
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     * @throws IOException if an I/O error occurs
     */
    public StreamingResponseBody getTelegramVideo(final String remoteId, final String accountId) throws ExecutionException, InterruptedException, IOException {
        final Client client = clientManager.getClient(accountId);
        CompletableFuture<TdApi.File> future = new CompletableFuture<>();
        client.send(new TdApi.GetRemoteFile(remoteId, new TdApi.FileTypeVideo()), result -> {
            if (result instanceof TdApi.File) {
                future.complete((TdApi.File) result);
            } else if (result instanceof TdApi.Error) {
                future.completeExceptionally(new RuntimeException("Error fetching video:" + ((TdApi.Error) result).message));
            } else {
                future.completeExceptionally(new RuntimeException("Failed to fetch video"));
            }
        });

        final Path path = getFilePath(future.get(), client, 200, 500);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("File still not downloaded after waiting");
        }

        final File file = path.toFile();

        return outputStream -> {
            try (InputStream inputStream = new FileInputStream(file)) {
                inputStream.transferTo(outputStream);
                outputStream.flush();
            } finally {
                Files.deleteIfExists(path);
                log.info("Temporary file {} deleted after download.", path);
            }
        };

    }

    /**
     * Retrieves a document from Telegram chats
     * Gets the RemoteFile by remoteId, downloads it temporarily, and converts it to StreamingResponseBody
     *
     * @param remoteId the Telegram document identifier
     * @param accountId the Telegram account identifier
     * @return StreamingResponseBody for the document
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     * @throws IOException if an I/O error occurs
     */
    public StreamingResponseBody getTelegramDocument(final String remoteId, final String accountId) throws ExecutionException, InterruptedException, IOException {
        final Client client = clientManager.getClient(accountId);
        CompletableFuture<TdApi.File> future = new CompletableFuture<>();
        client.send(new TdApi.GetRemoteFile(remoteId, new TdApi.FileTypeDocument()), result -> {
            if (result instanceof TdApi.File) {
                future.complete((TdApi.File) result);
            } else if (result instanceof TdApi.Error) {
                future.completeExceptionally(new RuntimeException("Error fetching image:" + ((TdApi.Error) result).message));
            } else {
                future.completeExceptionally(new RuntimeException("Failed to fetch image"));
            }
        });

        final Path path = getFilePath(future.get(), client, 10, 300);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("File still not downloaded after waiting");
        }

        final File file = path.toFile();

        return outputStream -> {
            try (InputStream inputStream = new FileInputStream(file)) {
                inputStream.transferTo(outputStream);
                outputStream.flush();
            } finally {
                Files.deleteIfExists(path);
                log.info("Temporary file {} deleted after download.", path);
            }
        };
    }

}
