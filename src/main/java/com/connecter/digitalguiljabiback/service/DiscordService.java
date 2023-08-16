package com.connecter.digitalguiljabiback.service;

import com.connecter.digitalguiljabiback.config.properties.DiscordProperties;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Service
public class DiscordService {

    private final DiscordProperties discordProperties;

    public void callErrorEvent(String message) {
        JSONObject data = new JSONObject();

        data.put("content", "서버에서 예상치 못한 에러가 발생하였습니다!");

        JSONObject[] embeds = new JSONObject[1];
        embeds[0] = new JSONObject();

        embeds[0].put("title", "에러");
        embeds[0].put("description", "발생일시: " + LocalDateTime.now() + "\n\n" + message);
        data.put("embeds", embeds);

        send(data);
    }

    public void send(JSONObject jsonObject) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
        restTemplate.postForObject(discordProperties.getWebhookURL(), entity, String.class);
    }
    
}
