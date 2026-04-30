package com.ntt.report.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.report.model.entity.ReportEvent;
import com.ntt.report.repository.ReportEventRepository;
import io.reactivex.rxjava3.core.Single;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;

@ExtendWith(MockitoExtension.class)
class ReportKafkaConsumerTest {

  @Mock private ReactiveKafkaConsumerTemplate<String, String> consumerTemplate;
  @Mock private ReportEventRepository reportEventRepository;
  @Mock private ReceiverOffset receiverOffset;

  private ReportKafkaConsumer consumer;

  @BeforeEach
  void setUp() {
    consumer = new ReportKafkaConsumer(consumerTemplate, reportEventRepository, new ObjectMapper());
  }

  @Test
  void startKafkaConsumerStoresReportEventWithExtractedProducts() {
    ReceiverRecord<String, String> record =
        new ReceiverRecord<>(
            new ConsumerRecord<>(
                "transactions-executed-topic",
                0,
                0,
                "key-account",
                "{\"sourceId\":\"account-1\",\"targetId\":\"account-2\",\"creditId\":\"credit-1\"}"),
            receiverOffset);
    when(consumerTemplate.receiveAutoAck()).thenReturn(Flux.just(record));
    when(reportEventRepository.save(any(ReportEvent.class)))
        .thenAnswer(
            invocation -> {
              ReportEvent event = invocation.getArgument(0);
              event.setId("event-1");
              return Single.just(event);
            });

    consumer.startKafkaConsumer();

    ArgumentCaptor<ReportEvent> eventCaptor = ArgumentCaptor.forClass(ReportEvent.class);
    verify(reportEventRepository).save(eventCaptor.capture());
    org.assertj.core.api.Assertions.assertThat(eventCaptor.getValue().getTopic())
        .isEqualTo("transactions-executed-topic");
    org.assertj.core.api.Assertions.assertThat(eventCaptor.getValue().getInvolvedProducts())
        .contains("account-1", "account-2", "credit-1", "key-account");
  }

  @Test
  void startKafkaConsumerStoresEventWhenPayloadIsNotJson() {
    ReceiverRecord<String, String> record =
        new ReceiverRecord<>(
            new ConsumerRecord<>("topic", 0, 0, "fallback-key", "not-json"), receiverOffset);
    when(consumerTemplate.receiveAutoAck()).thenReturn(Flux.just(record));
    when(reportEventRepository.save(any(ReportEvent.class)))
        .thenAnswer(invocation -> Single.just(invocation.getArgument(0)));

    consumer.startKafkaConsumer();

    ArgumentCaptor<ReportEvent> eventCaptor = ArgumentCaptor.forClass(ReportEvent.class);
    verify(reportEventRepository).save(eventCaptor.capture());
    org.assertj.core.api.Assertions.assertThat(eventCaptor.getValue().getInvolvedProducts())
        .containsExactly("fallback-key");
  }
}
