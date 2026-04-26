package com.ntt.report.repository;

import com.ntt.report.model.entity.ReportEvent;
import io.reactivex.rxjava3.core.Flowable;
import java.time.LocalDateTime;
import org.springframework.data.repository.reactive.RxJava3CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportEventRepository extends RxJava3CrudRepository<ReportEvent, String> {

  // Para el requerimiento: Reporte general por producto en intervalo de tiempo
  Flowable<ReportEvent> findByInvolvedProductsContainingAndTimestampBetweenOrderByTimestampDesc(
      String productId, LocalDateTime startDate, LocalDateTime endDate);

  // Para el requerimiento: Últimos 10 movimientos de la tarjeta (débito/crédito) o producto
  Flowable<ReportEvent> findTop10ByInvolvedProductsContainingOrderByTimestampDesc(String productId);
}
