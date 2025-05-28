package com.pryabykh.intershop.client;

import com.pryabykh.intershop.client.api.PaymentsApi;
import com.pryabykh.intershop.client.domain.PayPost200Response;
import com.pryabykh.intershop.client.domain.PayPostRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PaymentApiClient extends PaymentsApi {
    private final ReactiveOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    @Value("${payment.url:#{null}}")
    private String basePath;

    public PaymentApiClient(ReactiveOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    @PostConstruct
    public void init() {
        if (basePath != null) {
            this.getApiClient().setBasePath(this.basePath);
        }
    }

    @Override
    public Mono<PayPost200Response> payPost(PayPostRequest payPostRequest) {
        return oAuth2AuthorizedClientManager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId("intershop")
                        .principal("system")
                        .build())
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(OAuth2AccessToken::getTokenValue)
                .flatMap(accessToken -> {
                    ApiClient apiClient = new ApiClient();
                    apiClient.setBasePath(basePath);
                    apiClient.setBearerToken(accessToken);
                    PaymentsApi paymentsApi = new PaymentsApi(apiClient);
                    return paymentsApi.payPost(payPostRequest);
                });
    }
}
