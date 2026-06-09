package com.codequest.problem;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.codequest.problem.dto.PistonRequest;
import com.codequest.problem.dto.PistonResponse;

@Component
public class PistonHttpClient implements PistonClient {

    private final RestClient restClient;
    private final String pistonBaseUrl;

    public PistonHttpClient(
            RestClient.Builder restClientBuilder,
            @Value("${piston.base-url:https://emkc.org/api/v2/piston}") String pistonBaseUrl
    ) {
        this.restClient = restClientBuilder.build();
        this.pistonBaseUrl = pistonBaseUrl;
    }

    @Override
    public PistonResponse execute(PistonRequest request) {
        try {
            PistonResponse response = restClient.post()
                    .uri(buildExecuteUri(pistonBaseUrl))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(PistonResponse.class);

            if (response == null || response.run() == null) {
                throw new PistonException(PistonException.Category.INVALID_RESPONSE, "Piston response did not contain run output.");
            }

            return response;
        } catch (PistonException ex) {
            throw ex;
        } catch (RestClientResponseException ex) {
            throw new PistonException(
                    PistonException.Category.REQUEST_FAILURE,
                    "Piston request failed with HTTP status " + ex.getStatusCode().value() + ".",
                    ex
            );
        } catch (RestClientException ex) {
            throw new PistonException(PistonException.Category.REQUEST_FAILURE, "Piston request failed.", ex);
        }
    }

    static URI buildExecuteUri(String baseUrl) {
        String normalizedBaseUrl = baseUrl == null ? "" : baseUrl.trim();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(normalizedBaseUrl);
        String path = builder.build().getPath();

        if (path == null || path.isBlank() || "/".equals(path)) {
            path = "/api/v2/piston";
        } else if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return builder
                .replacePath(path + "/execute")
                .replaceQuery(null)
                .buildAndExpand(Map.of())
                .toUri();
    }
}
