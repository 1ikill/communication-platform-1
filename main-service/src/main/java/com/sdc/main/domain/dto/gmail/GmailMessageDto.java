package com.sdc.main.domain.dto.gmail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Gmail message DTO
 * @since 12.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Gmail message DTO")
public class GmailMessageDto {
    @Schema(description = "Id")
    private String id;

    @Schema(description = "Thread id")
    private String threadId;

    @Schema(description = "Label ids")
    private List<String> labelIds;

    @Schema(description = "Snippet")
    private String snippet;

    @Schema(description = "Internal date")
    private Date internalDate;

    @Schema(description = "From")
    private String from;

    @Schema(description = "To")
    private String to;

    @Schema(description = "Subject")
    private String subject;

    @Schema(description = "Body")
    private String body;

    @Schema(description = "Unread")
    private boolean unread;

    @Schema(description = "Attachments")
    private List<AttachmentDto> attachments;
}
