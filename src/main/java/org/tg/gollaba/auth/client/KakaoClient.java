package org.tg.gollaba.auth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.tg.gollaba.common.support.Status;

@Component
@RequiredArgsConstructor
public class KakaoClient {
    private final String disconnectUrl = "https://kapi.kakao.com/v1/user/unlink";
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

        var response = restTemplate.postForEntity(disconnectUrl, requestEntity, String.class);

        if(!response.getStatusCode().is2xxSuccessful()){
            throw new org.tg.gollaba.auth.client.KakaoClient.InvalidDisconnectException(Status.KAKAO_DISCONNECT_FAIL.message());
        }
    }

    static class InvalidDisconnectException extends RuntimeException {
        public InvalidDisconnectException(String message) {
            super(message);
        }
    }
}