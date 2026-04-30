package com.ntt.report.service.impl;

import com.ntt.report.model.entity.ReportEvent;
import com.ntt.report.repository.ReportEventRepository;
import com.ntt.report.service.ReportService;
import io.reactivex.rxjava3.core.Flowable;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

  private final ReportEventRepository reportEventRepository;

  @Override
  public Flowable<ReportEvent> getReportByProductAndDateRange(
      String productId, LocalDateTime startDate, LocalDateTime endDate) {
    log.info("Consultando reporte por producto y rango. productId={}", productId);
    return reportEventRepository
        .findByInvolvedProductsContainingAndTimestampBetweenOrderByTimestampDesc(
            productId, startDate, endDate);
  }

  @Override
  public Flowable<ReportEvent> getLast10MovementsByProduct(String productId) {
    log.info("Consultando ultimos 10 movimientos. productId={}", productId);
    return reportEventRepository.findTop10ByInvolvedProductsContainingOrderByTimestampDesc(
        productId);
  }
}
