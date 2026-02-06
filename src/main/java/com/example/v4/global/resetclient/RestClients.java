package com.example.v4.global.resetclient;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.v4.global.exception.RestClientErrorException;
import com.example.v4.global.exception.RestServerErrorException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RestClients {

    private final RestClient restClient;

    /**
     * 생성자를 통해 RestClient를 주입받으며, 기본적으로 공통 에러 핸들러를 등록합니다.
     * Bean 등록 시 설정을 따르되, 여기서 공통 statusHandler를 강제할 수 있습니다.
     */
    public RestClients(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    HttpStatusCode status = response.getStatusCode();
                    log.error("API Call Failed. Status: {}, Method: {}, URI: {}",
                            status, request.getMethod(), request.getURI());

                    if (status.is4xxClientError()) {
                        throw new RestClientErrorException(status);
                    } else {
                        throw new RestServerErrorException("External Server Error occurred");
                    }
                })
                .build();
    }

    // ---- GET Methods ----

    /**
     * GET 요청 (응답 본문 반환)
     *
     * <pre>
     * 예:
     *   User res = restClients.get("/api/user/{id}", User.class, 3);
     * </pre>
     */
    public <T> T get(String uri, Class<T> responseType, Object... uriVariables) {
        return restClient.get()
                .uri(uri, uriVariables)
                .retrieve()
                .body(responseType);
    }

    /**
     * GET 요청 (응답 본문 없음)
     *
     * <pre>
     * 예: restClients.getBodiless("/api/ping");
     * </pre>
     */
    public void getBodiless(String uri, Object... uriVariables) {
        restClient.get()
                .uri(uri, uriVariables)
                .retrieve()
                .toBodilessEntity();
    }

    // ---- POST Methods ----

    /**
     * POST 요청 (본문 없음, 응답 없음)
     *
     * <pre>
     * 예: restClients.post("/api/sync/start");
     * </pre>
     */
    public void post(String uri, Object... uriVariables) {
        restClient.post()
                .uri(uri, uriVariables)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * POST 요청 (JSON 본문 전송, 응답 없음)
     *
     * <pre>
     * 예: restClients.post("/api/user", userDto);
     * </pre>
     */
    public void post(String uri, Object body, Object... uriVariables) {
        restClient.post()
                .uri(uri, uriVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * POST 요청 (JSON 본문 전송, 응답 본문 반환)
     *
     * <pre>
     * 예:
     *   User res = restClients.post("/api/user", userDto, User.class);
     * </pre>
     */
    public <T> T post(String uri, Object body, Class<T> responseType, Object... uriVariables) {
        return restClient.post()
                .uri(uri, uriVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(responseType);
    }

    // ---- PUT Methods ----

    /**
     * PUT 요청 (본문 없음)
     *
     * <pre>
     * 예: restClients.put("/api/task/{id}/complete", 42);
     * </pre>
     */
    public void put(String uri, Object... uriVariables) {
        restClient.put()
                .uri(uri, uriVariables)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * PUT 요청 (JSON 본문 전송, 응답 없음)
     *
     * <pre>
     * 예: restClients.put("/api/post/{id}", postDto, 1);
     * </pre>
     */
    public void put(String uri, Object body, Object... uriVariables) {
        restClient.put()
                .uri(uri, uriVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * PUT 요청 (JSON 본문 전송, 응답 본문 반환)
     *
     * <pre>
     * 예:
     *   Post res = restClients.put("/api/post/{id}", postDto, Post.class, 1);
     * </pre>
     */
    public <T> T put(String uri, Object body, Class<T> responseType, Object... uriVariables) {
        return restClient.put()
                .uri(uri, uriVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(responseType);
    }

    // ---- DELETE Methods ----

    /**
     * DELETE 요청
     *
     * <pre>
     * 예: restClients.delete("/api/task/{id}", 11);
     * </pre>
     */
    public void delete(String uri, Object... uriVariables) {
        restClient.delete()
                .uri(uri, uriVariables)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * DELETE 요청 (응답 본문 반환)
     *
     * <pre>
     * 예:
     *   Message res = restClients.delete("/api/task/{id}", Message.class, 11);
     * </pre>
     */
    public <T> T delete(String uri, Class<T> responseType, Object... uriVariables) {
        return restClient.delete()
                .uri(uri, uriVariables)
                .retrieve()
                .body(responseType);
    }
}
