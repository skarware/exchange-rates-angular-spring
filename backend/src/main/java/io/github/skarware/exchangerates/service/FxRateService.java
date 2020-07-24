package io.github.skarware.exchangerates.service;

import io.github.skarware.exchangerates.model.FxRate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

public interface FxRateService {

    FxRate findById(Long id);

    FxRate findRxRateByDateBySourceCurrencyAndTargetCurrency(String effectiveDate, String fromCurrency, String toCurrency);

    Collection<FxRate> find100LatestFxRatesBySourceCurrencyAndTargetCurrency(String fromCurrency, String toCurrency);

    Collection<FxRate> getFxRatesForToday();

    Collection<FxRate> getFxRateByTargetCurrencyForToday(String targetCurrency);

    FxRate save(FxRate fxRate);

    FxRate addFxRate(String sourceCurrency, String targetCurrency, BigDecimal exchangeRate, Date effectiveDate);

}
