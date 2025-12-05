package com.sdc.whatsapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GraphApiHelper {

    private static final String GRAPH_BASE = "https://graph.facebook.com/v19.0";
    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Verify that the provided token can query the phone number id.
     */
    public boolean verifyPhoneNumberId(String token, String phoneNumberId) {
        try {
            String url = GRAPH_BASE + "/" + phoneNumberId + "?fields=id,display_phone_number&access_token=" + token;
            String body = rest.getForObject(url, String.class);
            JsonNode node = mapper.readTree(body);
            return node.has("id") && node.get("id").asText().equals(phoneNumberId);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Get phone numbers for a WABA
     */
    public JsonNode listPhoneNumbersForWaba(String token, String wabaId) throws Exception {
        String url = GRAPH_BASE + "/" + wabaId + "/phone_numbers?access_token=" + token;
        String body = rest.getForObject(url, String.class);
        return mapper.readTree(body);
    }
}
