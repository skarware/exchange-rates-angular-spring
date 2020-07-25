package io.github.skarware.exchangerates.service;

import io.github.skarware.exchangerates.model.CurrencyModel;

import java.util.Collection;

public interface CurrencyModelService {

    CurrencyModel findByNumericCode(int numericCode);

    CurrencyModel findByAlphabeticCode(String alphabeticCode);

    CurrencyModel getOrMake(String currencyCode);

    Collection<CurrencyModel> getAllByOrderByAlphabeticCode();

    CurrencyModel save(CurrencyModel currencyModel);

    CurrencyModel add(String currencyCode);

    // Convenience method, uses Example obj with passed CurrencyModel as probe (to match CurrencyModel properties, Alphabetic and Numeric codes)
    boolean exists(CurrencyModel currencyModel);

    // Convenience method for new CurrencyModelRepository().existsByNumericCode(int)
    boolean existsByMatchingNumericCode(CurrencyModel currencyModel);

    // Convenience method for new CurrencyModelRepository().existsByAlphabeticCode(string)
    boolean existsByMatchingAlphabeticCode(CurrencyModel currencyModel);

}
