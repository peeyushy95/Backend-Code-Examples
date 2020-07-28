package com.commons.webClient;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class BlockingRestClient<T, V> {

    CloseableHttpClient httpClient = HttpClientBuilder
            .create()
            .build();
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

    private RestTemplate restTemplate = new RestTemplate(factory);

    private ResponseErrorHandler responseHandler = new ResponseErrorHandler() {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {

            if (response.getStatusCode() != HttpStatus.OK) {
                System.out.println(response.getStatusText());
            }
            return response.getStatusCode() == HttpStatus.OK ? false : true;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            // TODO Auto-generated method stub

        }
    };

    public V execute(RequestDetails requestDetails, T data, Class<V> genericClass) throws ResourceAccessException, Exception {

        restTemplate.setErrorHandler(responseHandler);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        headers.set("X-Requested-With", "XMLHttpRequest");

        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<V> response = restTemplate.exchange(requestDetails.getUrl(), requestDetails.getRequestType(),
                entity, genericClass);

        return response.getBody();
    }
}
