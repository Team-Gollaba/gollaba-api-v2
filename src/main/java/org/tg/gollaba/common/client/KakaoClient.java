package org.tg.gollaba.common.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.tg.gollaba.common.exception.ServerException;
import org.tg.gollaba.common.support.Status;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoClient {
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.kakao.admin-key}")
    private String kakaoAdminKey;

    public void disconnect(String providerId){
        var id = Long.parseLong(providerId);
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

        var requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("target_id_type", "user_id");
        requestBody.add("target_id", String.valueOf(id));

        var requestEntity = new HttpEntity<>(requestBody, headers);
        var response = restTemplate.postForEntity(
            "https://kapi.kakao.com/v1/user/unlink",
            requestEntity,
            String.class
        );

        if (response.getStatusCode().isError()) {
            log.error("[KakaoClient][disconnect] Failed to disconnect Kakao user: {}", response.getBody());
            throw new ServerException(Status.KAKAO);
        }
    }
}
