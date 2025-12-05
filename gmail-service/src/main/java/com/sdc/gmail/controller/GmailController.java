package com.sdc.gmail.controller;

import com.sdc.gmail.service.GmailCredentialsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Gmail-service main controller.
 * @since 11.2025
 */
@Slf4j
@RestController
@RequestMapping("/gmail")
@RequiredArgsConstructor
public class GmailController {
    private final GmailCredentialsService gmailCredentialsService;

    /**
     * Method for authentication of gmail account.
     * @param file MultipartFile with gmail credentials.
     * @return webhook url on Google Cloud auth.
     */
    @Operation(description = "Authenticate gmail account")
    @PostMapping("/auth")
    public String getAuthUrl(
            @RequestParam("file")
            final MultipartFile file) throws Exception {
        log.info("Received request POST /gmail/auth");
        String url = gmailCredentialsService.buildAuthorizationUrl(file);
        log.info("Produced response 200 for POST /gmail/auth with body:{}", url);
        return url;
    }

    /**
     * Callback for google cloud console authentication webhook.
     * @param code authentication code.
     * @param state authentication state.
     * @return authenticated user email address.
     */
    @Operation(description = "Callback for google cloud console")
    @GetMapping("/oauth/google/callback")
    public String callback(
            @RequestParam("code")
            final String code,
            @RequestParam(value = "state", required = false)
            final String state) throws Exception {
        log.info("Received request GET /oauth/google/callback");
        final String url = gmailCredentialsService.handleOAuthCallback(code, state);
        log.info("Produced response 200 for GET /oauth/google/callback with body:{}", url);
        return url;
    }

    /**
     * Method for sending gmail message.
     * @param accountId gmail accountId.
     * @param to message receiver email address.
     * @param subject message subject.
     * @param body message body.
     */
    @Operation(description = "Send message")
    @PostMapping("/send")
    public void sendEmail(
            @RequestParam
            final Long accountId,
            @RequestParam
            final String to,
            @RequestParam
            final String subject,
            @RequestParam
            final String body) throws Exception {
        log.info("Received request POST /gmail/send with accountId:{}, to:{}, subject:{}, message:{}", accountId, to, subject, body);
        gmailCredentialsService.sendEmail(accountId, to, subject, body);
        log.info("Produced response 200 for POST /gmail/send");
    }
}
