package com.commons.webClient;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@Builder
@Getter

public class RequestDetails {
    private String url;
    private HttpMethod requestType;

}

