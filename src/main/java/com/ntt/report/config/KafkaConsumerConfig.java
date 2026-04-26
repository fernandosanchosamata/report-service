package com.ntt.report.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

@Configuration
public class KafkaConsumerConfig {

  @Bean
  public ReceiverOptions<String, String> kafkaReceiverOptions(KafkaProperties kafkaProperties) {
    ReceiverOptions<String, String> basicReceiverOptions =
        ReceiverOptions.create(kafkaProperties.buildConsumerProperties(null));

    List<String> topics =
        Arrays.asList(
            "customers-topic",
            "transactions-executed-topic",
            "apply-payment-command-topic",
            "payment-applied-topic",
            "payment-rejected-topic",
            "debt-status-checked-topic");

    return basicReceiverOptions.subscription(topics);
  }

  @Bean
  public ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate(
      ReceiverOptions<String, String> kafkaReceiverOptions) {
    return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
  }
}
