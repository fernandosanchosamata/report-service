package com.ntt.report.controller;

import com.ntt.report.model.entity.ReportEvent;
import com.ntt.report.service.ReportService;
import io.reactivex.rxjava3.core.Flowable;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

  private final ReportService reportService;

  @GetMapping("/products/{productId}")
  public Flowable<ReportEvent> getReportByProductAndDateRange(
      @PathVariable String productId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    log.info("Solicitud recibida para reporte por rango. productId={}", productId);
    return reportService.getReportByProductAndDateRange(productId, startDate, endDate);
  }

  @GetMapping("/products/{productId}/last-10")
  public Flowable<ReportEvent> getLast10MovementsByProduct(@PathVariable String productId) {
    log.info("Solicitud recibida para ultimos movimientos. productId={}", productId);
    return reportService.getLast10MovementsByProduct(productId);
  }
}
