package org.example.config;

import co.tryterra.terraclient.TerraClientFactory;
import co.tryterra.terraclient.WebhookHandlerUtility;
import co.tryterra.terraclient.api.TerraClientV2;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("terra")
public class TerraConfig {
    @NotBlank
    private String xApiKey;
    @NotBlank
    private String devId;
    @NotBlank
    private String webhookSecret;

    @Bean
    public TerraClientV2 terraClientV2() {
        return TerraClientFactory.getClientV2(xApiKey, devId);
    }

    @Bean
    public WebhookHandlerUtility webhookHandlerUtility() {
        System.out.println(webhookSecret);
        return new WebhookHandlerUtility(webhookSecret);
    }
}
