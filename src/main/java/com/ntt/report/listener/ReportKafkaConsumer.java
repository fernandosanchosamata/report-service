package com.ntt.report.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.report.model.entity.ReportEvent;
import com.ntt.report.repository.ReportEventRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.adapter.rxjava.RxJava3Adapter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportKafkaConsumer {

  private final ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;
  private final ReportEventRepository reportEventRepository;
  private final ObjectMapper objectMapper;

  @EventListener(ApplicationStartedEvent.class)
  public void startKafkaConsumer() {
    reactiveKafkaConsumerTemplate
        .receiveAutoAck()
        .doOnNext(
            record ->
                log.info(
                    "Recibido evento de Kafka. Topico: {}, Key: {}", record.topic(), record.key()))
        .flatMap(
            record -> {
              List<String> involvedProducts = extractInvolvedProducts(record.value());
              if (record.key() != null && !involvedProducts.contains(record.key())) {
                involvedProducts.add(record.key());
              }

              ReportEvent reportEvent =
                  ReportEvent.builder()
                      .topic(record.topic())
                      .eventKey(record.key())
                      .payload(record.value())
                      .involvedProducts(involvedProducts)
                      .timestamp(LocalDateTime.now())
                      .build();

              // Adaptamos de RxJava Single a Reactor Mono para mantener el flujo reactivo de Spring
              // Kafka
              return RxJava3Adapter.singleToMono(
                  reportEventRepository
                      .save(reportEvent)
                      .doOnSuccess(
                          saved -> log.info("Evento guardado en MongoDB con ID: {}", saved.getId()))
                      .doOnError(e -> log.error("Error guardando evento en MongoDB", e)));
            })
        .doOnError(e -> log.error("Error en el consumidor de Kafka", e))
        .subscribe(); // Iniciar consumo en background
  }

  private List<String> extractInvolvedProducts(String payload) {
    List<String> products = new ArrayList<>();
    try {
      JsonNode node = objectMapper.readTree(payload);

      // Extraer IDs conocidos de cualquier payload
      extractIfPresent(node, "sourceId", products);
      extractIfPresent(node, "targetId", products);
      extractIfPresent(node, "creditId", products);
      extractIfPresent(node, "accountId", products);
      extractIfPresent(node, "customerId", products);
      extractIfPresent(node, "transactionId", products);
      extractIfPresent(node, "mainAccountId", products); // Débito

    } catch (Exception e) {
      log.warn(
          "No se pudo parsear el payload como JSON para extraer productos: {}", e.getMessage());
    }
    return products;
  }

  private void extractIfPresent(JsonNode node, String fieldName, List<String> products) {
    if (node.has(fieldName) && !node.get(fieldName).isNull()) {
      String value = node.get(fieldName).asText();
      if (!products.contains(value)) {
        products.add(value);
      }
    }
  }
}
