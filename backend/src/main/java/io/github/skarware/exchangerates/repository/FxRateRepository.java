package io.github.skarware.exchangerates.repository;

import io.github.skarware.exchangerates.model.CurrencyModel;
import io.github.skarware.exchangerates.model.FxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;

public interface FxRateRepository extends JpaRepository<FxRate, Long> {

    FxRate findByEffectiveDateAndSourceCurrencyAndTargetCurrency(Date effectiveDate, CurrencyModel sourceCurrency, CurrencyModel targetCurrency);

    Collection<FxRate> findFirst100BySourceCurrencyAndTargetCurrencyOrderByIdDesc(CurrencyModel sourceCurrency, CurrencyModel targetCurrency);

    @Query(value = "SELECT r.EFFECTIVE_DATE FROM FX_RATES r ORDER BY r.ID DESC LIMIT 1", nativeQuery = true)
    Date selectDateFromFxRatesLimitOneOrderByIdDesc();

    Collection<FxRate> findByEffectiveDate(Date effectiveDate);

    @Query(value = "SELECT * FROM FX_RATES WHERE TARGET_CURRENCY = :numericCode ORDER BY ID DESC LIMIT 1", nativeQuery = true)
    FxRate selectFxRateByTargetCurrencyNumericCodeLimitOneOrderByIdDesc(@Param("numericCode") int numericCode);

}
