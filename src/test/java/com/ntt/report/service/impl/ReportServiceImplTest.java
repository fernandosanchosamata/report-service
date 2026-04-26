package com.ntt.report.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ntt.report.model.entity.ReportEvent;
import com.ntt.report.repository.ReportEventRepository;
import io.reactivex.rxjava3.core.Flowable;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

  @Mock private ReportEventRepository reportEventRepository;

  @Test
  void getReportByProductAndDateRangeDelegatesToRepository() {
    ReportServiceImpl service = new ReportServiceImpl(reportEventRepository);
    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now();
    ReportEvent event = reportEvent();
    when(reportEventRepository
            .findByInvolvedProductsContainingAndTimestampBetweenOrderByTimestampDesc(
                "account-1", start, end))
        .thenReturn(Flowable.just(event));

    var result =
        service.getReportByProductAndDateRange("account-1", start, end).toList().blockingGet();

    assertThat(result).containsExactly(event);
    verify(reportEventRepository)
        .findByInvolvedProductsContainingAndTimestampBetweenOrderByTimestampDesc(
            "account-1", start, end);
  }

  @Test
  void getLast10MovementsByProductDelegatesToRepository() {
    ReportServiceImpl service = new ReportServiceImpl(reportEventRepository);
    ReportEvent event = reportEvent();
    when(reportEventRepository.findTop10ByInvolvedProductsContainingOrderByTimestampDesc(
            "account-1"))
        .thenReturn(Flowable.just(event));

    var result = service.getLast10MovementsByProduct("account-1").toList().blockingGet();

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
