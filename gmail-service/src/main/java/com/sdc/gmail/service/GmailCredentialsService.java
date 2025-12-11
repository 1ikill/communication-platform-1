package com.sdc.gmail.service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.gmail.model.Message;
import com.sdc.gmail.domain.dto.GmailAccountInfoDto;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sdc.gmail.config.security.CurrentUser;
import com.sdc.gmail.domain.model.GmailCredentials;
import com.sdc.gmail.repository.GmailCredentialsRepository;
import com.sdc.gmail.utils.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.api.services.gmail.Gmail;
import jakarta.mail.Session;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Base64;


import com.google.api.client.json.jackson2.JacksonFactory;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailCredentialsService {
    private final static String SCOPES =
            "openid email profile https://www.googleapis.com/auth/gmail.readonly https://www.googleapis.com/auth/gmail.modify https://www.googleapis.com/auth/gmail.send";
    private final static String WEB = "web";
    private final static String CLIENT_ID = "client_id";
    private final static String CLIENT_SECRET = "client_secret";
    private final static String INSTALLED = "installed";
    private final static String ACCESS_TYPE_OFFLINE = "offline";
    private final static String PROMPT = "prompt";
    private final static String CONSENT = "consent";
    private final static String USER_ID_PARAM = "userId=";
    private final static String ACCOUNT_ID_PARAM = "&accountId=";
    private final static String ACCOUNT_ID = "accountId";
    private final static String EMAIL = "email";
    private final static String APPLICATION_NAME = "CommunicationPlatform";
    private final static String TOKEN_SERVER_URL = "https://oauth2.googleapis.com/token";
    private final static String USER_INFO_URL = "https://openidconnect.googleapis.com/v1/userinfo";
    private final static String GMAIL_USER_ME = "me";
    private final static String REDIRECT_URI = "http://localhost:8086/gmail/oauth/google/callback";
    private final static String SCOPE_DELIMITER = " ";
    private final static String STATE_DELIMITER = "&";
    private final static String KEY_VALUE_DELIMITER = "=";
    private final static int KEY_VALUE_PARTS = 2;

    private final static Long MIN_EXPIRES_IN = 60L;

    private final GmailCredentialsRepository gmailCredentialsRepository;
    private final CurrentUser currentUser;
    private final CryptoUtils cryptoUtils;

    /**
     * Method for creation and saving to db gmail credentials.
     * @param userId user id.
     * @param clientId gmail client id.
     * @param clientSecret gmail client secret
     * @return GmailCredentials object.
     */
    private GmailCredentials createCredentials(final Long userId, final String clientId, final String clientSecret) throws Exception {
        final GmailCredentials credentials = new GmailCredentials();
        credentials.setUserId(userId);
        credentials.setClientId(cryptoUtils.encrypt(clientId));
        credentials.setClientSecret(cryptoUtils.encrypt(clientSecret));
        credentials.setScopes(SCOPES);
        return gmailCredentialsRepository.save(credentials);
    }

    /**
     * Method for building google cloud authorization url from gmail client secret file.
     * @param file file with gmail client secret.
     * @return google cloud authorization url.
     */
    public String buildAuthorizationUrl(final MultipartFile file) throws Exception {
        final Long userId = currentUser.getId();
        final String json = new String(file.getBytes(), StandardCharsets.UTF_8);
        final JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        final JsonObject web = root.has(WEB) ? root.getAsJsonObject(WEB) : root.getAsJsonObject(INSTALLED);
        final String clientId = web.get(CLIENT_ID).getAsString();
        final String clientSecret = web.get(CLIENT_SECRET).getAsString();

        final GmailCredentials credentials = createCredentials(userId, clientId, clientSecret);

        final List<String> scopes = Arrays.asList(SCOPES.split(SCOPE_DELIMITER));
        final GoogleAuthorizationCodeRequestUrl url = new GoogleAuthorizationCodeRequestUrl(
                clientId,
                REDIRECT_URI,
                scopes
        )
                .setAccessType(ACCESS_TYPE_OFFLINE)
                .set(PROMPT, CONSENT);

        final String state = USER_ID_PARAM + userId + ACCOUNT_ID_PARAM + credentials.getId();
        url.setState(state);

        return url.build();
    }

    /**
     * Handler method for OAuth callback from google.
     * @param code authorization code.
     * @param state authorization state.
     * @return user's email address.
     */
    public String handleOAuthCallback(final String code, final String state) throws Exception {
        final Map<String, String> stateMap = parseState(state);
        final Long accountId = Long.parseLong(stateMap.get(ACCOUNT_ID));

        final GmailCredentials account = gmailCredentialsRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Failed to find account with id: " + accountId));

        final String clientId = cryptoUtils.decrypt(account.getClientId());
        final String clientSecret = cryptoUtils.decrypt(account.getClientSecret());

        final HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        final GoogleTokenResponse tokenResponse = getTokenResponse(clientId, clientSecret, code, transport, jsonFactory);

        final String accessToken = tokenResponse.getAccessToken();
        final String refreshToken = tokenResponse.getRefreshToken();
        final Long expiresIn = tokenResponse.getExpiresInSeconds();

        if (refreshToken == null && account.getRefreshToken() == null) {
            throw new RuntimeException("No refresh token returned. Re-authorize with prompt=consent.");
        }

        final Credential credential = buildCredential(clientId, clientSecret, transport, jsonFactory);
        credential.setFromTokenResponse(tokenResponse);

        final String email = fetchUserEmail(transport, jsonFactory, credential);

        account.setEmailAddress(email);
        account.setAccessToken(cryptoUtils.encrypt(accessToken));

        if (refreshToken != null) {
            account.setRefreshToken(cryptoUtils.encrypt(refreshToken));
        }

        account.setTokenExpiry(LocalDateTime.now().plusSeconds(expiresIn));
        gmailCredentialsRepository.save(account);

        return email;
    }

    /**
     * Method for sending gmail message.
     * @param accountId gmail accountId.
     * @param to message receiver email address.
     * @param subject message subject.
     * @param bodyText message body.
     */
    public void sendEmail(final Long accountId,
                          final String to,
                          final String subject,
                          final String bodyText) throws Exception {

        final GmailCredentials account = getAndValidateAccount(accountId);
        final Gmail gmail = buildGmailServiceForAccount(account);

        final Properties props = new Properties();
        final Session session = Session.getInstance(props, null);

        final MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(account.getEmailAddress()));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);

        sendMimeMessage(gmail, email);
    }

    /**
     * Method for sending gmail message with file attachment.
     * @param accountId gmail accountId.
     * @param to message receiver email address.
     * @param subject message subject.
     * @param bodyText message body.
     * @param file file to attach (image, video, document).
     */
    public void sendEmailWithFile(final Long accountId,
                                   final String to,
                                   final String subject,
                                   final String bodyText,
                                   final MultipartFile file) throws Exception {

        final GmailCredentials account = getAndValidateAccount(accountId);
        final Gmail gmail = buildGmailServiceForAccount(account);

        final Properties props = new Properties();
        final Session session = Session.getInstance(props, null);

        final MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(account.getEmailAddress()));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        final jakarta.mail.Multipart multipart = new jakarta.mail.internet.MimeMultipart();

        final jakarta.mail.internet.MimeBodyPart textPart = new jakarta.mail.internet.MimeBodyPart();
        textPart.setText(bodyText);
        multipart.addBodyPart(textPart);

        final jakarta.mail.internet.MimeBodyPart attachmentPart = new jakarta.mail.internet.MimeBodyPart();
        final jakarta.activation.DataSource source = new jakarta.mail.util.ByteArrayDataSource(file.getBytes(), file.getContentType());
        attachmentPart.setDataHandler(new jakarta.activation.DataHandler(source));
        attachmentPart.setFileName(file.getOriginalFilename());
        multipart.addBodyPart(attachmentPart);

        email.setContent(multipart);

        sendMimeMessage(gmail, email);
    }

    /**
     * Get connected gmail accounts.
     * @return GmailAccountIfoDto list.
     */
    public List<GmailAccountInfoDto> getMe() {
        final Long userId = currentUser.getId();
        return gmailCredentialsRepository.findAllByUserId(userId).stream()
                .map(cred -> new GmailAccountInfoDto(cred.getId(), cred.getEmailAddress()))
                .toList();
    }

    /**
     * Get and validate Gmail account with token refresh
     * @param accountId Gmail account ID
     * @return Validated Gmail credentials
     */
    private GmailCredentials getAndValidateAccount(final Long accountId) throws Exception {
        final GmailCredentials account = gmailCredentialsRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found for id: " + accountId));

        final String accessToken = cryptoUtils.decrypt(account.getAccessToken());
        final String refreshToken = cryptoUtils.decrypt(account.getRefreshToken());
        final String clientId = cryptoUtils.decrypt(account.getClientId());
        final String clientSecret = cryptoUtils.decrypt(account.getClientSecret());

        final HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        final Credential credential = buildCredential(clientId, clientSecret, transport, jsonFactory);
        credential.setAccessToken(accessToken);
        credential.setRefreshToken(refreshToken);
        validateTokenExpiry(credential, account);

        return account;
    }

    /**
     * Build Gmail service instance for account
     * @param account Gmail credentials
     * @return Configured Gmail service
     */
    private Gmail buildGmailServiceForAccount(final GmailCredentials account) throws Exception {
        final String accessToken = cryptoUtils.decrypt(account.getAccessToken());
        final String refreshToken = cryptoUtils.decrypt(account.getRefreshToken());
        final String clientId = cryptoUtils.decrypt(account.getClientId());
        final String clientSecret = cryptoUtils.decrypt(account.getClientSecret());

        final HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        final Credential credential = buildCredential(clientId, clientSecret, transport, jsonFactory);
        credential.setAccessToken(accessToken);
        credential.setRefreshToken(refreshToken);

        return new Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Fetch user email from Google OAuth2 API
     * @param transport HTTP transport
     * @param jsonFactory JSON factory
     * @param credential OAuth2 credential
     * @return User email address
     */
    private String fetchUserEmail(final HttpTransport transport, final JsonFactory jsonFactory, final Credential credential) throws Exception {
        final Oauth2 oauth2 = new Oauth2.Builder(transport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .setHttpRequestInitializer(credential)
                .build();

        final GenericUrl userInfoUrl = new GenericUrl(USER_INFO_URL);
        final HttpRequest request = transport.createRequestFactory(credential).buildGetRequest(userInfoUrl);
        final String json = request.execute().parseAsString();

        final Map<String, Object> userInfo = jsonFactory.fromString(json, Map.class);
        return (String) userInfo.get(EMAIL);
    }

    /**
     * Send MIME message via Gmail API
     * @param gmail Gmail service instance
     * @param email MIME message to send
     */
    private void sendMimeMessage(final Gmail gmail, final MimeMessage email) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        final String raw = Base64.getUrlEncoder().encodeToString(baos.toByteArray());

        final Message message = new Message();
        message.setRaw(raw);

        gmail.users().messages().send(GMAIL_USER_ME, message).execute();
    }

    /**
     * Validate token expiry and refresh if needed
     * @param credential OAuth2 credential
     * @param account Gmail credentials
     */
    private void validateTokenExpiry(final Credential credential, final GmailCredentials account) throws Exception {
        final Long expiresIn = credential.getExpiresInSeconds();
        if (expiresIn == null || expiresIn <= MIN_EXPIRES_IN) {
            final boolean refreshed = credential.refreshToken();
            if (refreshed) {
                account.setAccessToken(cryptoUtils.encrypt(credential.getAccessToken()));
                account.setTokenExpiry(LocalDateTime.now().plusSeconds(credential.getExpiresInSeconds()));
                gmailCredentialsRepository.save(account);
            } else {
                throw new RuntimeException("Failed to refresh access token. Reauthorize Gmail account.");
            }
        }
    }

    /**
     * Get OAuth2 token response from Google
     * @param clientId OAuth client ID
     * @param clientSecret OAuth client secret
     * @param code Authorization code
     * @param transport HTTP transport
     * @param jsonFactory JSON factory
     * @return Token response
     */
    private GoogleTokenResponse getTokenResponse(final String clientId, final String clientSecret, final String code,
                                                 final HttpTransport transport, final JsonFactory jsonFactory) throws IOException {
        return new GoogleAuthorizationCodeTokenRequest(
                transport,
                jsonFactory,
                clientId,
                clientSecret,
                code,
                REDIRECT_URI
        ).execute();
    }

    /**
     * Build OAuth2 credential
     * @param clientId OAuth client ID
     * @param clientSecret OAuth client secret
     * @param transport HTTP transport
     * @param jsonFactory JSON factory
     * @return Configured credential
     */
    private Credential buildCredential(final String clientId, final String clientSecret,
                                       final HttpTransport transport, final JsonFactory jsonFactory) {
        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(transport)
                .setJsonFactory(jsonFactory)
                .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                .setTokenServerUrl(new GenericUrl(TOKEN_SERVER_URL))
                .build();
    }

    /**
     * Parse state parameter from OAuth callback
     * @param state State string
     * @return Parsed state map
     */
    private Map<String,String> parseState(final String state) {
        final Map<String,String> map = new HashMap<>();
        if (isNull(state)) {
            return map;
        }
        for (final String part : state.split(STATE_DELIMITER)) {
            final String[] kv = part.split(KEY_VALUE_DELIMITER, KEY_VALUE_PARTS);
            if (kv.length == KEY_VALUE_PARTS) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}
