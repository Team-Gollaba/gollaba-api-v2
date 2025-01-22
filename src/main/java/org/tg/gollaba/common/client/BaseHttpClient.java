package org.tg.gollaba.common.client;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.tg.gollaba.common.exception.HttpClientException;
import org.tg.gollaba.common.support.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseHttpClient {
    private final RestTemplate restTemplate;

    public BaseHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        restTemplate.setRequestFactory(
            new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory())
        );
    }

    protected <T> T exchange(RequestEntity<?> request,
                             Class<T> responseType,
                             Function<ClientHttpResponse, ? extends HttpClientException> clientErrorFunction,
                             Function<ClientHttpResponse, ? extends HttpClientException> serverErrorFunction) {
        return exchange(request, responseType, s -> false, clientErrorFunction, serverErrorFunction);
    }

    protected <T> T exchange(RequestEntity<?> request,
                             Class<T> responseType,
                             Predicate<String> errorPredicate,
                             Function<ClientHttpResponse, ? extends HttpClientException> clientErrorFunction,
                             Function<ClientHttpResponse, ? extends HttpClientException> serverErrorFunction) {
        if (Objects.equals(request.getHeaders().getContentType(), MediaType.APPLICATION_JSON)) {
            MDC.put("requestBody", JsonUtils.stringify(request.getBody()));
        }

        var errorHandler = new CustomResponseErrorHandler(
            errorPredicate,
            clientErrorFunction,
            serverErrorFunction
        );
        restTemplate.setErrorHandler(errorHandler);

        var response = restTemplate.exchange(request, String.class);

        return JsonUtils.parse(response.getBody(), responseType);
    }

    protected String extractResponseBody(ClientHttpResponse response) {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected int extractResponseStatusCode(ClientHttpResponse response) {
        try {
            return response.getRawStatusCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Slf4j
    private static class CustomResponseErrorHandler implements ResponseErrorHandler {
        private final Predicate<String> errorPredicate;
        private final Function<ClientHttpResponse, ? extends RuntimeException> clientErrorFunction;
        private final Function<ClientHttpResponse, ? extends RuntimeException> serverErrorFunction;

        public CustomResponseErrorHandler(Predicate<String> errorPredicate,
                                          Function<ClientHttpResponse,? extends HttpClientException> clientErrorFunction,
                                          Function<ClientHttpResponse, ? extends HttpClientException> serverErrorFunction) {
            this.errorPredicate = errorPredicate;
            this.clientErrorFunction = clientErrorFunction;
            this.serverErrorFunction = serverErrorFunction;
        }

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            var statusCode = response.getStatusCode();

            if (statusCode == HttpStatus.OK && errorPredicate.test(extractResponseBody(response))) {
                return true;
            }

            return statusCode.is4xxClientError() || statusCode.is5xxServerError();
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            var requestBody = Optional.ofNullable(MDC.get("requestBody")).orElse("");

            if (response.getStatusCode().is4xxClientError()) {
                log.warn("[HttpClient 4xx Error] requestBody: {} responseBody: {}", requestBody, extractResponseBody(response));
                throw clientErrorFunction.apply(response);
            } else if (response.getStatusCode().is5xxServerError()) {
                log.error("[HttpClient 5xx Error] requestBody: {} responseBody: {}", requestBody, extractResponseBody(response));
                throw serverErrorFunction.apply(response);
            } else {
                log.warn("[HttpClient Unexpected Error] requestBody: {} responseBody: {}", requestBody, extractResponseBody(response));
                throw clientErrorFunction.apply(response);
            }
        }

        @Override
        public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
            log.warn("[HttpClient Error] URI: {}, Method: {}", url, method);
            handleError(response);
        }

        private String extractResponseBody(ClientHttpResponse response) throws IOException {
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
}
