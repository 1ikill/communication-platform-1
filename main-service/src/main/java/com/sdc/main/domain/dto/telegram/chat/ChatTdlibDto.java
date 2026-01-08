package com.sdc.main.domain.dto.telegram.chat;

import com.sdc.main.domain.dto.telegram.chat.list.ChatListTdlib;
import com.sdc.main.domain.dto.telegram.chat.type.ChatTypeTdlib;
import com.sdc.main.domain.dto.telegram.message.MessageTdlibDto;
import com.sdc.main.domain.dto.telegram.message.sending.MessageSenderTdlib;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description ="Дто чата")
public class ChatTdlibDto {
    @Schema(description = "Идентификатор чата")
    private Long id;

    @Schema(description = "Тип чата")
    private ChatTypeTdlib type;

    @Schema(description = "Название чата")
    private String title;

    @Schema(description = "Разрешения чата")
    private ChatPermissionsDto permissions;

    @Schema(description = "Последнее сообщение")
    private MessageTdlibDto lastMessage;

    @Schema(description = "Позиции чата")
    private List<ChatPositionDto> positions;

    @Schema(description = "Списки чатов")
    private List<ChatListTdlib> chatLists;

    @Schema(description = "Отправитель")
    private MessageSenderTdlib messageSenderId;

    @Schema(description = "Непрочитанно")
    private Boolean isMarkedAsUnread;

    @Schema(description = "Есть отложенные сообщения")
    private Boolean hasScheduledMessages;

    @Schema(description = "Может быть удален для всех")
    private Boolean canBeDeletedForAllUsers;

    @Schema(description = "Выключить уведомления по умолчанию")
    private Boolean defaultDisableNotification;

    @Schema(description = "Количество непрочитанных")
    private Integer unreadCount;

    @Schema(description = "Идентификатор последнего прочитанного полученного сообщения")
    private Long lastReadInboxMessageId;

    @Schema(description = "Идентификатор последнего прочитанного отправленного сообщения")
    private Long lastReadOutboxMessageId;

    @Schema(description = "Количество непрочитанных упоминаний")
    private Integer unreadMentionCount;

    @Schema(description = "Количество непрочитанных реакций")
    private Integer unreadReactionCount;

    @Schema(description = "Настройки уведомлений")
    private ChatNotificationSettingsDto notificationSettings;

    @Schema(description = "Таймер автоудаления сообщений")
    private Integer messageAutoDeleteTime;

    @Schema(description = "Текущие запросы на присоеденение")
    private ChatJoinRequestsInfoDto pendingJoinRequests;

    @Schema(description = "Идентификатор отмеченного сообщения")
    private Long replyMarkupMessageId;

    @Schema(description = "Данные клиента")
    private String clientData;

    @Schema(description = "Ссылка на фото профиля")
    private String profilePhotoUrl;
}
