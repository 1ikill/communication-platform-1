package com.sdc.telegram.service;

import com.sdc.telegram.config.TelegramClientManager;
import com.sdc.telegram.config.security.CurrentUser;
import com.sdc.telegram.domain.dto.TelegramAccountDto;
import com.sdc.telegram.domain.dto.TelegramNotificationDto;
import com.sdc.telegram.domain.dto.tdlib.TelegramChatFolderDto;
import com.sdc.telegram.domain.dto.tdlib.chat.ChatTdlibDto;
import com.sdc.telegram.domain.dto.tdlib.message.MessageTdlibDto;
import com.sdc.telegram.domain.dto.tdlib.user.UserTdlibDto;
import com.sdc.telegram.domain.mapper.chat.ChatTdlibMapper;
import com.sdc.telegram.domain.mapper.message.MessageTdlibMapper;
import com.sdc.telegram.domain.mapper.user.UserTdlibMapper;
import com.sdc.telegram.domain.model.TelegramCredentials;
import com.sdc.telegram.repository.TelegramCredentialsRepository;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramServiceTest {
    
    @Mock
    private TelegramClientManager clientManager;
    
    @Mock
    private ChatTdlibMapper chatMapper;
    
    @Mock
    private MessageTdlibMapper messageMapper;
    
    @Mock
    private UserTdlibMapper userMapper;
    
    @Mock
    private CurrentUser currentUser;
    
    @Mock
    private TelegramCredentialsRepository credentialsRepository;
    
    @Mock
    private Client client;
    
    private TelegramService telegramService;
    
    private static final String ACCOUNT_ID = "testAccount";
    private static final Long CHAT_ID = 123456L;
    private static final Long USER_ID = 789L;
    private static final Long MESSAGE_ID = 999L;
    private static final String USERNAME = "testuser";
    
    @BeforeEach
    void setUp() {
        telegramService = new TelegramService(
            clientManager, chatMapper, messageMapper, userMapper, currentUser, credentialsRepository
        );
        when(clientManager.getClient(ACCOUNT_ID)).thenReturn(client);
    }
    
    @Test
    void findAllChats_WithValidLimit_ShouldReturnChats() throws Exception {
        TdApi.Chat chat1 = new TdApi.Chat();
        chat1.id = 1L;
        chat1.title = "Chat 1";
        
        TdApi.Chat chat2 = new TdApi.Chat();
        chat2.id = 2L;
        chat2.title = "Chat 2";
        
        TdApi.Chats chats = new TdApi.Chats();
        chats.chatIds = new long[]{1L, 2L};
        chats.totalCount = 2;
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(chats);
            return null;
        }).when(client).send(any(TdApi.GetChats.class), any());
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(chat1);
            return null;
        }).when(client).send(any(TdApi.GetChat.class), any());
        
        when(chatMapper.toDto(anyList())).thenReturn(new ArrayList<>());
        
        List<ChatTdlibDto> result = telegramService.findAllChats(10, new TdApi.ChatListMain(), ACCOUNT_ID);
        
        assertNotNull(result);
        verify(client, atLeastOnce()).send(any(TdApi.GetChats.class), any());
    }
    
    @Test
    void findUserChatIdByUsername_WithValidUsername_ShouldReturnChatId() throws Exception {
        TdApi.Chat chat = new TdApi.Chat();
        chat.id = CHAT_ID;
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            TdApi.Chats emptyChats = new TdApi.Chats();
            emptyChats.chatIds = new long[]{};
            emptyChats.totalCount = 0;
            handler.onResult(emptyChats);
            return null;
        }).when(client).send(any(TdApi.GetChats.class), any());
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(chat);
            return null;
        }).when(client).send(any(TdApi.SearchPublicChat.class), any());
        
        Long result = telegramService.findUserChatIdByUsername(USERNAME, ACCOUNT_ID);
        
        assertEquals(CHAT_ID, result);
        verify(client).send(any(TdApi.SearchPublicChat.class), any());
    }
    
    @Test
    void findUserChatIdByUsername_WithUserNotFound_ShouldThrowException() throws Exception {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            TdApi.Chats emptyChats = new TdApi.Chats();
            emptyChats.chatIds = new long[]{};
            emptyChats.totalCount = 0;
            handler.onResult(emptyChats);
            return null;
        }).when(client).send(any(TdApi.GetChats.class), any());
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Error(404, "User not found"));
            return null;
        }).when(client).send(any(TdApi.SearchPublicChat.class), any());
        
        assertThrows(Exception.class,
            () -> telegramService.findUserChatIdByUsername(USERNAME, ACCOUNT_ID));
    }
    
    @Test
    void createChatWithUser_WithValidUserId_ShouldReturnChatId() throws Exception {
        TdApi.Chat chat = new TdApi.Chat();
        chat.id = CHAT_ID;
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(chat);
            return null;
        }).when(client).send(any(TdApi.CreatePrivateChat.class), any());
        
        Long result = telegramService.createChatWithUser(USER_ID, ACCOUNT_ID);
        
        assertEquals(CHAT_ID, result);
        verify(client).send(any(TdApi.CreatePrivateChat.class), any());
    }
    
    @Test
    void createChatWithUser_WithFailure_ShouldThrowException() throws Exception {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Error(400, "Failed to create chat"));
            return null;
        }).when(client).send(any(TdApi.CreatePrivateChat.class), any());
        
        assertThrows(Exception.class,
            () -> telegramService.createChatWithUser(USER_ID, ACCOUNT_ID));
    }
    
    @Test
    void sendTextMessage_WithValidData_ShouldSendMessage() throws Exception {
        String messageText = "Hello, World!";
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            TdApi.Chats emptyChats = new TdApi.Chats();
            emptyChats.chatIds = new long[]{};
            emptyChats.totalCount = 0;
            handler.onResult(emptyChats);
            return null;
        }).when(client).send(any(TdApi.GetChats.class), any());
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            TdApi.Message message = new TdApi.Message();
            message.id = MESSAGE_ID;
            handler.onResult(message);
            return null;
        }).when(client).send(any(TdApi.SendMessage.class), any());
        
        telegramService.sendTextMessage(CHAT_ID, messageText, ACCOUNT_ID);
        
        verify(client).send(any(TdApi.SendMessage.class), any());
    }
    
    @Test
    void getAccountInfo_WithValidAccountId_ShouldReturnUserDto() throws Exception {
        TdApi.User user = new TdApi.User();
        user.id = USER_ID;
        user.firstName = "John";
        user.lastName = "Doe";
        
        UserTdlibDto expectedDto = new UserTdlibDto();
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(user);
            return null;
        }).when(client).send(any(TdApi.GetMe.class), any());
        
        when(userMapper.toDto(user, ACCOUNT_ID)).thenReturn(expectedDto);
        
        UserTdlibDto result = telegramService.getAccountInfo(ACCOUNT_ID);
        
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(client).send(any(TdApi.GetMe.class), any());
        verify(userMapper).toDto(user, ACCOUNT_ID);
    }
    
    @Test
    void getAccountInfo_WithFailure_ShouldThrowException() {
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Error(500, "Internal error"));
            return null;
        }).when(client).send(any(TdApi.GetMe.class), any());
        
        assertThrows(Exception.class,
            () -> telegramService.getAccountInfo(ACCOUNT_ID));
    }
    
    @Test
    void getAllAccountsInfo_WithAuthorizedAccounts_ShouldReturnList() throws Exception {
        TelegramCredentials credentials = new TelegramCredentials();
        credentials.setAccountId(ACCOUNT_ID);
        credentials.setAccountName("Test Account");
        
        when(currentUser.getId()).thenReturn(USER_ID);
        when(credentialsRepository.findAllByUserId(USER_ID)).thenReturn(List.of(credentials));
        
        TdApi.User user = new TdApi.User();
        user.id = USER_ID;
        
        UserTdlibDto userDto = new UserTdlibDto();
        
        doAnswer(invocation -> {
            TdApi.Function function = invocation.getArgument(0);
            Client.ResultHandler handler = invocation.getArgument(1);
            
            if (function instanceof TdApi.GetAuthorizationState) {
                handler.onResult(new TdApi.AuthorizationStateReady());
            } else if (function instanceof TdApi.GetMe) {
                handler.onResult(user);
            }
            return null;
        }).when(client).send(any(), any());
        
        when(userMapper.toDto(user, ACCOUNT_ID)).thenReturn(userDto);
        
        List<TelegramAccountDto> result = telegramService.getAllAccountsInfo();
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(credentialsRepository).findAllByUserId(USER_ID);
    }
    
    @Test
    void findChatsNotifications_ShouldReturnNotifications() throws Exception {
        TdApi.Chat chat1 = new TdApi.Chat();
        chat1.id = 1L;
        chat1.unreadCount = 5;
        
        TdApi.Chat chat2 = new TdApi.Chat();
        chat2.id = 2L;
        chat2.unreadCount = 10;
        
        TdApi.Chats chats = new TdApi.Chats();
        chats.chatIds = new long[]{1L, 2L};
        chats.totalCount = 2;
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(chats);
            return null;
        }).when(client).send(any(TdApi.GetChats.class), any());
        
        doAnswer(invocation -> {
            TdApi.Function function = invocation.getArgument(0);
            Client.ResultHandler handler = invocation.getArgument(1);
            
            if (function instanceof TdApi.GetChat) {
                TdApi.GetChat getChat = (TdApi.GetChat) function;
                if (getChat.chatId == 1L) {
                    handler.onResult(chat1);
                } else {
                    handler.onResult(chat2);
                }
            }
            return null;
        }).when(client).send(any(TdApi.GetChat.class), any());
        
        List<TelegramNotificationDto> result = telegramService.findChatsNotifications(ACCOUNT_ID);
        
        assertNotNull(result);
        verify(client).send(any(TdApi.GetChats.class), any());
    }
    
    @Test
    void getChatFolders_WithAvailableFolders_ShouldReturnList() throws Exception {
        TdApi.ChatListFolder folder1 = new TdApi.ChatListFolder(1);
        TdApi.ChatListFolder folder2 = new TdApi.ChatListFolder(2);
        TdApi.ChatLists chatLists = new TdApi.ChatLists(new TdApi.ChatList[]{folder1, folder2});
        
        doAnswer(invocation -> {
            TdApi.Function function = invocation.getArgument(0);
            Client.ResultHandler handler = invocation.getArgument(1);
            
            if (function instanceof TdApi.GetChats) {
                TdApi.Chats emptyChats = new TdApi.Chats();
                emptyChats.chatIds = new long[]{};
                emptyChats.totalCount = 0;
                handler.onResult(emptyChats);
            } else if (function instanceof TdApi.GetChatListsToAddChat) {
                handler.onResult(chatLists);
            } else if (function instanceof TdApi.GetChatFolder) {
                TdApi.ChatFolder folder = new TdApi.ChatFolder();
                TdApi.ChatFolderName folderName = new TdApi.ChatFolderName();
                TdApi.FormattedText nameText = new TdApi.FormattedText();
                nameText.text = "Folder";
                folderName.text = nameText;
                folder.name = folderName;
                handler.onResult(folder);
            }
            return null;
        }).when(client).send(any(), any());
        
        List<TelegramChatFolderDto> result = telegramService.getChatFolders(ACCOUNT_ID);
        
        assertNotNull(result);
        verify(client).send(any(TdApi.GetChatListsToAddChat.class), any());
    }
    
    @Test
    void getChatFolders_WithNoFolders_ShouldReturnEmptyList() throws Exception {
        doAnswer(invocation -> {
            TdApi.Function function = invocation.getArgument(0);
            Client.ResultHandler handler = invocation.getArgument(1);
            
            if (function instanceof TdApi.GetChats) {
                TdApi.Chats emptyChats = new TdApi.Chats();
                emptyChats.chatIds = new long[]{};
                emptyChats.totalCount = 0;
                handler.onResult(emptyChats);
            } else if (function instanceof TdApi.GetChatListsToAddChat) {
                handler.onResult(new TdApi.ChatLists(new TdApi.ChatList[]{}));
            }
            return null;
        }).when(client).send(any(), any());
        
        List<TelegramChatFolderDto> result = telegramService.getChatFolders(ACCOUNT_ID);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    void createEmptyChat_ShouldSendDraftMessage() {
        doAnswer(invocation -> null)
            .when(client).send(any(TdApi.SetChatDraftMessage.class), any());
        
        telegramService.createEmptyChat(CHAT_ID, ACCOUNT_ID);
        
        verify(client).send(any(TdApi.SetChatDraftMessage.class), any());
    }
    
    @Test
    void deleteEmptyChat_ShouldDeleteDraftMessage() {
        doAnswer(invocation -> null)
            .when(client).send(any(TdApi.SetChatDraftMessage.class), any());
        
        telegramService.deleteEmptyChat(CHAT_ID, ACCOUNT_ID);
        
        verify(client).send(any(TdApi.SetChatDraftMessage.class), any());
    }
    
    @Test
    void setProfilePhoto_WithValidFile_ShouldSetPhoto() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", "test data".getBytes()
        );
        
        doAnswer(invocation -> {
            Client.ResultHandler handler = invocation.getArgument(1);
            handler.onResult(new TdApi.Ok());
            return null;
        }).when(client).send(any(TdApi.SetProfilePhoto.class), any());
        
        telegramService.setProfilePhoto(file, ACCOUNT_ID);
        
        verify(client).send(any(TdApi.SetProfilePhoto.class), any());
    }
}
