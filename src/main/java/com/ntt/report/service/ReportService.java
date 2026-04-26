package com.ntt.report.service;

import com.ntt.report.model.entity.ReportEvent;
import io.reactivex.rxjava3.core.Flowable;
import java.time.LocalDateTime;

public interface ReportService {

  Flowable<ReportEvent> getReportByProductAndDateRange(
      String productId, LocalDateTime startDate, LocalDateTime endDate);

  Flowable<ReportEvent> getLast10MovementsByProduct(String productId);
}
