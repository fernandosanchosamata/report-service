package com.ntt.report.model.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_events")
public class ReportEvent {

  @Id private String id;

  private String topic;

  private String eventKey;

  // Almacenamos el JSON crudo para no acoplarnos a la estructura de otros microservicios
  private String payload;

  // Lista de IDs extraídos del payload (ej. accountId, creditId, etc.)
  private java.util.List<String> involvedProducts;

  private LocalDateTime timestamp;
}
