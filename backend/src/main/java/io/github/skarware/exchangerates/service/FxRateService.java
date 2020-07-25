package io.github.skarware.exchangerates.service;

import io.github.skarware.exchangerates.model.FxRate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

public interface FxRateService {

    FxRate findById(Long id);

    FxRate getByDateBySourceCurrencyAndTargetCurrency(String effectiveDate, String fromCurrency, String toCurrency);

    Collection<FxRate> getLatest100BySourceCurrencyAndTargetCurrency(String fromCurrency, String toCurrency);

    Collection<FxRate> getLatestFxRates();

    Date getLatestDataDate();

    Collection<FxRate> getLatestByTargetCurrency(String targetCurrency);

    FxRate save(FxRate fxRate);

    FxRate add(String sourceCurrency, String targetCurrency, BigDecimal exchangeRate, Date effectiveDate);

}
