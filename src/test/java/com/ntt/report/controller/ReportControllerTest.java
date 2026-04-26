package com.ntt.report.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ntt.report.model.entity.ReportEvent;
import com.ntt.report.service.ReportService;
import io.reactivex.rxjava3.core.Flowable;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

  @Mock private ReportService reportService;

  @InjectMocks private ReportController controller;

  @Test
  void getReportByProductAndDateRangeReturnsEvents() {
    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now();
    ReportEvent event = reportEvent();
    when(reportService.getReportByProductAndDateRange("account-1", start, end))
        .thenReturn(Flowable.just(event));

    var result =
        controller.getReportByProductAndDateRange("account-1", start, end).toList().blockingGet();

    assertThat(result).containsExactly(event);
    verify(reportService).getReportByProductAndDateRange("account-1", start, end);
  }

  @Test
  void getLast10MovementsByProductReturnsEvents() {
    ReportEvent event = reportEvent();
    when(reportService.getLast10MovementsByProduct("account-1")).thenReturn(Flowable.just(event));

    var result = controller.getLast10MovementsByProduct("account-1").toList().blockingGet();

    assertThat(result).containsExactly(event);
  }

  private ReportEvent reportEvent() {
    return ReportEvent.builder()
        .id("event-1")
        .topic("transactions-executed-topic")
        .eventKey("tx-1")
        .payload("{}")
        .involvedProducts(List.of("account-1"))
        .timestamp(LocalDateTime.now())
        .build();
  }
}
