package com.sdc.telegram.service;

import com.sdc.telegram.config.TelegramClientManager;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramFileServiceTest {
    
    @Mock
    private TelegramClientManager clientManager;
    
    @Mock
    private Client client;
    
    @TempDir
    Path tempDir;
    
    private TelegramFileService telegramFileService;
    
    private static final String ACCOUNT_ID = "testAccount";
    private static final String PHOTO_REMOTE_ID = "PHOTO_abc123";
    private static final String PROFILE_REMOTE_ID = "PROFILE_xyz789";
    private static final String VIDEO_REMOTE_ID = "video123";
    private static final String DOCUMENT_REMOTE_ID = "doc456";
    
    @BeforeEach
    void setUp() {
        telegramFileService = new TelegramFileService(clientManager);
        when(clientManager.getClient(ACCOUNT_ID)).thenReturn(client);
    }
    
    @Test
    void getTelegramImage_WithPhotoType_ShouldReturnImageBytes() throws Exception {
        Path testFile = tempDir.resolve("test.jpg");
        byte[] testData = "test image data".getBytes();
        Files.write(testFile, testData);
        
        TdApi.File mockFile = new TdApi.File();
        mockFile.id = 1;
        mockFile.local = new TdApi.LocalFile();
        mockFile.local.path = testFile.toString();
        mockFile.local.isDownloadingCompleted = true;
        mockFile.remote = new TdApi.RemoteFile();
        mockFile.remote.id = "abc123";
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(mockFile);
            return null;
        }).when(client).send(any(TdApi.GetRemoteFile.class), any());
        
        byte[] result = telegramFileService.getTelegramImage(PHOTO_REMOTE_ID, ACCOUNT_ID);
        
        assertNotNull(result);
        assertArrayEquals(testData, result);
        verify(client).send(any(TdApi.GetRemoteFile.class), any());
    }
    
    @Test
    void getTelegramImage_WithProfileType_ShouldReturnImageBytes() throws Exception {
        Path testFile = tempDir.resolve("profile.jpg");
        byte[] testData = "profile image data".getBytes();
        Files.write(testFile, testData);
        
        TdApi.File mockFile = new TdApi.File();
        mockFile.id = 2;
        mockFile.local = new TdApi.LocalFile();
        mockFile.local.path = testFile.toString();
        mockFile.local.isDownloadingCompleted = true;
        mockFile.remote = new TdApi.RemoteFile();
        mockFile.remote.id = "xyz789";
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(mockFile);
            return null;
        }).when(client).send(any(TdApi.GetRemoteFile.class), any());
        
        byte[] result = telegramFileService.getTelegramImage(PROFILE_REMOTE_ID, ACCOUNT_ID);
        
        assertNotNull(result);
        assertArrayEquals(testData, result);
    }
    
    @Test
    void getTelegramImage_WithErrorResponse_ShouldThrowException() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Error(404, "File not found"));
            return null;
        }).when(client).send(any(TdApi.GetRemoteFile.class), any());
        
        assertThrows(Exception.class,
            () -> telegramFileService.getTelegramImage(PHOTO_REMOTE_ID, ACCOUNT_ID));
    }
    
    @Test
    void getTelegramVideo_WithValidRemoteId_ShouldReturnStreamingBody() throws Exception {
        Path testFile = tempDir.resolve("video.mp4");
        byte[] testData = "video data".getBytes();
        Files.write(testFile, testData);
        
        TdApi.File mockFile = new TdApi.File();
        mockFile.id = 3;
        mockFile.local = new TdApi.LocalFile();
        mockFile.local.path = testFile.toString();
        mockFile.local.isDownloadingCompleted = true;
        mockFile.remote = new TdApi.RemoteFile();
        mockFile.remote.id = VIDEO_REMOTE_ID;
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(mockFile);
            return null;
        }).when(client).send(any(TdApi.GetRemoteFile.class), any());
        
        StreamingResponseBody result = telegramFileService.getTelegramVideo(VIDEO_REMOTE_ID, ACCOUNT_ID);
        
        assertNotNull(result);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        result.writeTo(outputStream);
        assertArrayEquals(testData, outputStream.toByteArray());
    }
    
    @Test
    void getTelegramVideo_WithErrorResponse_ShouldThrowException() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Error(404, "Video not found"));
            return null;
        }).when(client).send(any(TdApi.GetRemoteFile.class), any());
        
        assertThrows(Exception.class,
            () -> telegramFileService.getTelegramVideo(VIDEO_REMOTE_ID, ACCOUNT_ID));
    }
    
    @Test
    void getTelegramDocument_WithValidRemoteId_ShouldReturnStreamingBody() throws Exception {
        Path testFile = tempDir.resolve("document.pdf");
        byte[] testData = "document data".getBytes();
        Files.write(testFile, testData);
        
        TdApi.File mockFile = new TdApi.File();
        mockFile.id = 4;
        mockFile.local = new TdApi.LocalFile();
        mockFile.local.path = testFile.toString();
        mockFile.local.isDownloadingCompleted = true;
        mockFile.remote = new TdApi.RemoteFile();
        mockFile.remote.id = DOCUMENT_REMOTE_ID;
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(mockFile);
            return null;
        }).when(client).send(any(TdApi.GetRemoteFile.class), any());
        
        StreamingResponseBody result = telegramFileService.getTelegramDocument(DOCUMENT_REMOTE_ID, ACCOUNT_ID);
        
        assertNotNull(result);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        result.writeTo(outputStream);
        assertArrayEquals(testData, outputStream.toByteArray());
    }
}
