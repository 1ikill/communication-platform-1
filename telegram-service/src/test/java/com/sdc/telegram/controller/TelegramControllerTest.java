package com.sdc.telegram.controller;

import com.sdc.telegram.domain.dto.TelegramAccountDto;
import com.sdc.telegram.domain.dto.TelegramNotificationDto;
import com.sdc.telegram.domain.dto.tdlib.TelegramChatFolderDto;
import com.sdc.telegram.domain.dto.tdlib.chat.ChatTdlibDto;
import com.sdc.telegram.domain.dto.tdlib.message.MessageTdlibDto;
import com.sdc.telegram.domain.dto.tdlib.user.UserTdlibDto;
import com.sdc.telegram.service.TelegramFileService;
import com.sdc.telegram.service.TelegramService;
import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TelegramControllerTest {
    
    private MockMvc mockMvc;
    
    @Mock
    private TelegramService telegramService;
    
    @Mock
    private TelegramFileService telegramFileService;
    
    @InjectMocks
    private TelegramController telegramController;
    
    private static final String ACCOUNT_ID = "testAccount";
    private static final Long CHAT_ID = 123456L;
    private static final Long USER_ID = 789L;
    private static final Long MESSAGE_ID = 999L;
    private static final String USERNAME = "testuser";
    private static final String REMOTE_ID = "remoteId123";
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(telegramController).build();
    }
    
    @Test
    void findAllChats_WithValidParams_ShouldReturnChats() throws Exception {
        List<ChatTdlibDto> chats = Arrays.asList(new ChatTdlibDto(), new ChatTdlibDto());
        when(telegramService.findAllChats(anyInt(), any(), anyString())).thenReturn(chats);
        
        mockMvc.perform(get("/telegram/main")
                .param("limit", "10")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
        
        verify(telegramService).findAllChats(eq(10), any(TdApi.ChatListMain.class), eq(ACCOUNT_ID));
    }
    
    @Test
    void findAllChats_WithArchiveChatListType_ShouldUseArchiveList() throws Exception {
        when(telegramService.findAllChats(anyInt(), any(), anyString()))
            .thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/telegram/archive")
                .param("limit", "10")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramService).findAllChats(eq(10), any(TdApi.ChatListArchive.class), eq(ACCOUNT_ID));
    }
    
    @Test
    void findAllChats_WithServiceException_ShouldThrowException() throws Exception {
        when(telegramService.findAllChats(anyInt(), any(), anyString()))
            .thenThrow(new RuntimeException("Service error"));
        
        assertThrows(Exception.class, () ->
            mockMvc.perform(get("/telegram/main")
                    .param("limit", "10")
                    .param("accountId", ACCOUNT_ID)));
    }
    
    @Test
    void getChatFolders_WithValidAccountId_ShouldReturnFolders() throws Exception {
        List<TelegramChatFolderDto> folders = Arrays.asList(
            new TelegramChatFolderDto("Folder1", 1),
            new TelegramChatFolderDto("Folder2", 2)
        );
        when(telegramService.getChatFolders(anyString())).thenReturn(folders);
        
        mockMvc.perform(get("/telegram/chats/folders")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        
        verify(telegramService).getChatFolders(ACCOUNT_ID);
    }
    
    @Test
    void findUserChatIdByUsername_WithValidUsername_ShouldReturnChatId() throws Exception {
        when(telegramService.findUserChatIdByUsername(anyString(), anyString()))
            .thenReturn(CHAT_ID);
        
        mockMvc.perform(get("/telegram/user-chat")
                .param("username", USERNAME)
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(CHAT_ID.toString()));
        
        verify(telegramService).findUserChatIdByUsername(USERNAME, ACCOUNT_ID);
    }
    
    @Test
    void createChatWithUser_WithValidUserId_ShouldReturnChatId() throws Exception {
        when(telegramService.createChatWithUser(anyLong(), anyString()))
            .thenReturn(CHAT_ID);
        
        mockMvc.perform(post("/telegram/create-chat/{userId}", USER_ID)
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(CHAT_ID.toString()));
        
        verify(telegramService).createChatWithUser(USER_ID, ACCOUNT_ID);
    }
    
    @Test
    void sendTextMessage_WithValidData_ShouldReturn200() throws Exception {
        String messageText = "Hello, World!";
        doNothing().when(telegramService)
            .sendTextMessage(anyLong(), anyString(), anyString());
        
        mockMvc.perform(post("/telegram/text")
                .param("chatId", CHAT_ID.toString())
                .param("messageText", messageText)
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramService).sendTextMessage(CHAT_ID, messageText, ACCOUNT_ID);
    }
    
    @Test
    void getMessage_WithValidIds_ShouldReturnMessage() throws Exception {
        MessageTdlibDto message = new MessageTdlibDto();
        when(telegramService.getTelegramMessage(anyLong(), anyLong(), anyString()))
            .thenReturn(message);
        
        mockMvc.perform(get("/telegram/{chatId}/message", CHAT_ID)
                .param("messageId", MESSAGE_ID.toString())
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramService).getTelegramMessage(MESSAGE_ID, CHAT_ID, ACCOUNT_ID);
    }
    
    @Test
    void getAllMessages_WithValidChatId_ShouldReturnMessages() throws Exception {
        List<MessageTdlibDto> messages = Arrays.asList(new MessageTdlibDto(), new MessageTdlibDto());
        when(telegramService.findAllMessages(anyLong(), anyInt(), anyString()))
            .thenReturn(messages);
        
        mockMvc.perform(get("/telegram/{chatId}/messages", CHAT_ID)
                .param("limit", "50")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
        
        verify(telegramService).findAllMessages(CHAT_ID, 50, ACCOUNT_ID);
    }
    
    @Test
    void getAccountInfo_WithValidAccountId_ShouldReturnUserInfo() throws Exception {
        UserTdlibDto user = new UserTdlibDto();
        when(telegramService.getAccountInfo(anyString())).thenReturn(user);
        
        mockMvc.perform(get("/telegram/account")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramService).getAccountInfo(ACCOUNT_ID);
    }
    
    @Test
    void getAllAccountsInfo_ShouldReturnAccountsList() throws Exception {
        List<TelegramAccountDto> accounts = Arrays.asList(
            new TelegramAccountDto("acc1", "Account 1", null),
            new TelegramAccountDto("acc2", "Account 2", null)
        );
        when(telegramService.getAllAccountsInfo()).thenReturn(accounts);
        
        mockMvc.perform(get("/telegram/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        
        verify(telegramService).getAllAccountsInfo();
    }
    
    @Test
    void getChatsNotifications_ShouldReturnNotifications() throws Exception {
        List<TelegramNotificationDto> notifications = Arrays.asList(
            new TelegramNotificationDto(ACCOUNT_ID, 1L, 5),
            new TelegramNotificationDto(ACCOUNT_ID, 2L, 10)
        );
        when(telegramService.findChatsNotifications(anyString())).thenReturn(notifications);
        
        mockMvc.perform(get("/telegram/notifications")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        
        verify(telegramService).findChatsNotifications(ACCOUNT_ID);
    }
    
    @Test
    void getTelegramImage_WithValidRemoteId_ShouldReturnImage() throws Exception {
        byte[] imageData = "image data".getBytes();
        when(telegramFileService.getTelegramImage(anyString(), anyString()))
            .thenReturn(imageData);
        
        mockMvc.perform(get("/telegram/file/{remoteId}", REMOTE_ID)
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageData));
        
        verify(telegramFileService).getTelegramImage(REMOTE_ID, ACCOUNT_ID);
    }
    
    @Test
    void setProfilePhoto_WithValidFile_ShouldReturn200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "test data".getBytes()
        );
        doNothing().when(telegramService).setProfilePhoto(any(), anyString());
        
        mockMvc.perform(multipart("/telegram/profile/images")
                .file("image", file.getBytes())
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramService).setProfilePhoto(any(), eq(ACCOUNT_ID));
    }
    
    @Test
    void sendImageMessage_WithValidData_ShouldReturn200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "image.jpg", "image/jpeg", "image data".getBytes()
        );
        doNothing().when(telegramService).sendImageMessage(anyLong(), any(), anyString(), anyString());
        
        mockMvc.perform(multipart("/telegram/image")
                .file("image", file.getBytes())
                .param("chatId", CHAT_ID.toString())
                .param("message", "Test message")
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramService).sendImageMessage(eq(CHAT_ID), any(), eq("Test message"), eq(ACCOUNT_ID));
    }
    
    @Test
    void createEmptyChat_WithValidChatId_ShouldReturn200() throws Exception {
        doNothing().when(telegramService).createEmptyChat(anyLong(), anyString());
        
        mockMvc.perform(post("/telegram/empty-chats")
                .param("chatId", CHAT_ID.toString())
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramService).createEmptyChat(CHAT_ID, ACCOUNT_ID);
    }
    
    @Test
    void deleteEmptyChat_WithValidChatId_ShouldReturn200() throws Exception {
        doNothing().when(telegramService).deleteEmptyChat(anyLong(), anyString());
        
        mockMvc.perform(delete("/telegram/empty-chats")
                .param("chatId", CHAT_ID.toString())
                .param("accountId", ACCOUNT_ID))
                .andExpect(status().isOk());
        
        verify(telegramService).deleteEmptyChat(CHAT_ID, ACCOUNT_ID);
    }
}
