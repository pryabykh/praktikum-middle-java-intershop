package com.pryabykh.intershop.client;

import com.pryabykh.intershop.client.api.BalanceApi;
import com.pryabykh.intershop.client.domain.BalanceGet200Response;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BalanceApiClient extends BalanceApi {
    private final ReactiveOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    @Value("${payment.url:#{null}}")
    private String basePath;

    public BalanceApiClient(ReactiveOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    @PostConstruct
    public void init() {
        if (basePath != null) {
            this.getApiClient().setBasePath(this.basePath);
        }
    }

    @Override
    public Mono<BalanceGet200Response> balanceGet(Long userId) {
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
                    BalanceApi balanceApi = new BalanceApi(apiClient);
                    return balanceApi.balanceGet(userId);
                });
    }
}
