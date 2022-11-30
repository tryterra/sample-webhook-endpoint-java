package org.example.controllers;

import co.tryterra.terraclient.WebhookHandlerUtility;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/consumeWebhook")
public class WebhookReceiverController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookReceiverController.class);

    private final WebhookHandlerUtility webhookHandlerUtility;

    public WebhookReceiverController(WebhookHandlerUtility webhookHandlerUtility) {
        this.webhookHandlerUtility = webhookHandlerUtility;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> consumeWebhook(
            @RequestBody @NotBlank String body,
            @RequestHeader("terra-signature") @NotBlank String signature
    ) {
        return Mono.just(body)
                .filter(raw -> webhookHandlerUtility.verifySignature(signature, raw))
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(
                        HttpStatusCode.valueOf(400), "Signature verification failed")))
                .mapNotNull(webhookHandlerUtility::parseWebhookPayload)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(
                        HttpStatusCode.valueOf(400), "Request body could not be parsed")))
                .doOnNext(payload -> LOGGER.atInfo()
                        .log("New payload received of type {}", payload.getType()))
                .thenReturn(ResponseEntity.noContent().build());
    }
}
