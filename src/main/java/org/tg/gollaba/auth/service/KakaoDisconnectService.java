package org.tg.gollaba.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.tg.gollaba.common.support.Status;

@Service
@RequiredArgsConstructor
public class KakaoDisconnectService {
//    @Value("https://kapi.kakao.com/v1/user/unlink")
    private final String disconnectUrl = "https://kapi.kakao.com/v1/user/unlink";

    @Value("${spring.security.oauth2.client.registration.kakao.admin-key}")
    private String kakaoAdminKey;

    public void disconnect(Long providerId){
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "KakaoAK " + kakaoAdminKey);

        var requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("target_id_type", "user_id");
        requestBody.add("target_id", String.valueOf(providerId));

        var restTemplate = new RestTemplate();
        var requestEntity = new HttpEntity<>(requestBody, headers);

        var response = restTemplate.postForEntity(disconnectUrl, requestEntity, String.class);

        if(!response.getStatusCode().is2xxSuccessful()){
            throw new InvalidDisconnectException(Status.KAKAO_DISCONNECT_FAIL.message());
        }
    }

    static class InvalidDisconnectException extends RuntimeException {
        public InvalidDisconnectException(String message) {
            super(message);
        }
    }
}
