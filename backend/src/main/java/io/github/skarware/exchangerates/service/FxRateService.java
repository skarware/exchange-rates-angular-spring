package io.github.skarware.exchangerates.service;

import io.github.skarware.exchangerates.model.FxRate;

import java.util.Collection;
import java.util.Date;

public interface FxRateService {

    FxRate findById(Long id);

    Collection<FxRate> getAllByOrderByEffectiveDate();

    Collection<FxRate> getAllByEffectiveDateGreaterThanEqualOrderByEffectiveDateDesc(Date date);

    FxRate save(FxRate fxRate);

    FxRate addFxRate(String sourceCurrency, String targetCurrency, String exchangeRate);

}
