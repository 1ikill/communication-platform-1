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

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramFileService {
    private final TelegramClientManager clientManager;

    /**
     * Метод для получения изображения из чатов телеграм
     * получает RemoteFile по remoteId, скачивает на время работы метода, парсит скачанный файл в byte[]
     * @param remoteId идентификатор изображения телеграм
     * @param accountId индентификатор аккаунта телеграм
     * @return byte[] изображение
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    //todo Рефактор сохранения изображения, придумать способ лучше.
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
     * Метод для получения видео из чатов телеграм
     * получает RemoteFile по remoteId, скачивает на время работы метода, парсит скачанный файл в StreamingResponseBody
     * @param remoteId идентификатор видео телеграм
     * @param accountId индентификатор аккаунта телеграм
     * @return StreamingResponseBody видео
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    //todo Рефактор сохранения видео, придумать способ лучше.
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
     * Метод для получения документов из чатов телеграм
     * получает RemoteFile по remoteId, скачивает на время работы метода, парсит скачанный файл в StreamingResponseBody
     * @param remoteId идентификатор документа телеграм
     * @param accountId индентификатор аккаунта телеграм
     * @return StreamingResponseBody документа
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     */
    //todo Рефактор сохранения документа, придумать способ лучше.
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
