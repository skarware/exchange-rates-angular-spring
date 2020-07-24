package io.github.skarware.exchangerates.repository;

import io.github.skarware.exchangerates.model.CurrencyModel;
import io.github.skarware.exchangerates.model.FxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Date;

public interface FxRateRepository extends JpaRepository<FxRate, Long> {

    FxRate findByEffectiveDateAndSourceCurrencyAndTargetCurrency(Date effectiveDate, CurrencyModel sourceCurrency, CurrencyModel targetCurrency);

    Collection<FxRate> findFirst100BySourceCurrencyAndTargetCurrencyOrderByIdDesc(CurrencyModel sourceCurrency, CurrencyModel targetCurrency);

    Collection<FxRate> findByEffectiveDate(Date effectiveDate);

}
