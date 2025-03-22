package com.ewallet.api.events;

import com.ewallet.api.dto.response.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class TransactionEventPublisher {

    private static final String TOPIC = "transactions";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public TransactionEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTransactionCreatedEvent(TransactionResponse transactionResponse) {
        TransactionEvent event = new TransactionEvent(
                "TRANSACTION_CREATED",
                transactionResponse
        );


        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, transactionResponse.getId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Transaction event sent successfully for ID: {}, offset: {}",
                            transactionResponse.getId(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send transaction event for ID: {}", transactionResponse.getId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error while publishing transaction event for ID: {}", transactionResponse.getId(), e);
        }
    }

    public void publishTransactionUpdatedEvent(TransactionResponse transactionResponse) {
        TransactionEvent event = new TransactionEvent(
                "TRANSACTION_UPDATED",
                transactionResponse
        );

        log.info("Publishing transaction updated event for transaction ID: {}", transactionResponse.getId());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, transactionResponse.getId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Transaction event sent successfully for ID: {}",
                        transactionResponse.getId());
            } else {
                log.error("Failed to send transaction event for ID: {}", transactionResponse.getId(), ex);
            }
        });
    }

    // Inner class for transaction events
    public static class TransactionEvent {
        private String eventType;
        private TransactionResponse transaction;

        public TransactionEvent() {}

        public TransactionEvent(String eventType, TransactionResponse transaction) {
            this.eventType = eventType;
            this.transaction = transaction;
        }

    }
}
