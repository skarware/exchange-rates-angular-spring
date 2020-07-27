package io.github.skarware.exchangerates.dto;

import lombok.Data;

@Data
// CurrencyExchangeDTO class to fill data from CurrencyExchangeService and return to a controller who in turn will return object in JSON format
public class CurrencyExchangeDTO {

    private final String amount;
    private final String from;
    private final String to;
    private final String convertedAmount;
    private final String commissionRate;
    private final String exchangeFee;
    private final String FxRatesSource;

}