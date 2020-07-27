package io.github.skarware.exchangerates.controller;

import io.github.skarware.exchangerates.dto.CurrencyExchangeDTO;
import io.github.skarware.exchangerates.service.CurrencyExchangeService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "${cross.origin.url}")
@RestController
public class CurrencyExchangeController {

    private final CurrencyExchangeService currencyExchangeService;

    public CurrencyExchangeController(CurrencyExchangeService currencyExchangeService) {
        this.currencyExchangeService = currencyExchangeService;
    }

    // Covert given currencies, optionally with commission rate, with latest exchange rates on database
    @GetMapping("/api/exchange/{fromCurrency}/{toCurrency}/{amount}/{commissionRate}")
    public CurrencyExchangeDTO getRxRateByDateBySourceCurrencyAndTargetCurrency(
            @PathVariable String fromCurrency,
            @PathVariable String toCurrency,
            @PathVariable String amount,
            @PathVariable String commissionRate
    ) {
        return currencyExchangeService.getConvertedAmount(amount, fromCurrency, toCurrency, commissionRate);
    }

}
