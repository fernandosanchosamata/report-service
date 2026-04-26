package com.ntt.report.service.impl;

import com.ntt.report.model.entity.ReportEvent;
import com.ntt.report.repository.ReportEventRepository;
import com.ntt.report.service.ReportService;
import io.reactivex.rxjava3.core.Flowable;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

  private final ReportEventRepository reportEventRepository;

  @Override
  public Flowable<ReportEvent> getReportByProductAndDateRange(
      String productId, LocalDateTime startDate, LocalDateTime endDate) {
    return reportEventRepository
        .findByInvolvedProductsContainingAndTimestampBetweenOrderByTimestampDesc(
            productId, startDate, endDate);
  }

  @Override
  public Flowable<ReportEvent> getLast10MovementsByProduct(String productId) {
    return reportEventRepository.findTop10ByInvolvedProductsContainingOrderByTimestampDesc(
        productId);
  }
}
