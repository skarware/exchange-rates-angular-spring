package io.github.skarware.exchangerates.repository;

import io.github.skarware.exchangerates.model.FxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Date;

public interface FxRateRepository extends JpaRepository<FxRate, Long> {

    Collection<FxRate> getAllByOrderByEffectiveDate();

    Collection<FxRate> getAllByEffectiveDateGreaterThanEqualOrderByEffectiveDateDesc(Date date);

}
