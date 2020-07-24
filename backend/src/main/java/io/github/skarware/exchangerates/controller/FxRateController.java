package io.github.skarware.exchangerates.controller;

import io.github.skarware.exchangerates.model.FxRate;
import io.github.skarware.exchangerates.service.FxRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class FxRateController {

    private final FxRateService fxRateService;

    @Autowired
    public FxRateController(FxRateService fxRateService) {
        this.fxRateService = fxRateService;
    }

    // Get latest day's FxRates from database ordered alphabetically
    @GetMapping("/api/fxrates")
    public Collection<FxRate> getFxRatesForToday() {
        return fxRateService.getFxRatesForToday();
    }

    // Get latest FxRate/s by given target currency (source should be base currency, by default EUR)
    @GetMapping("/api/fxrates/{targetCurrrency}")
    public Collection<FxRate> getFxRateByTargetCurrencyForToday(@PathVariable String targetCurrrency) {
        return fxRateService.getFxRateByTargetCurrencyForToday(targetCurrrency);
    }

    // Get latest FxRates by given currencies
    @GetMapping("/api/fxrates/{fromCurrency}/{toCurrency}")
    public Collection<FxRate> getLatestFxRatesBySourceCurrencyAndTargetCurrency(@PathVariable String fromCurrency, @PathVariable String toCurrency) {
        return fxRateService.find100LatestFxRatesBySourceCurrencyAndTargetCurrency(fromCurrency, toCurrency);
    }

    // Get FxRate by given currencies and date
    @GetMapping("/api/fxrates/{fromCurrency}/{toCurrency}/{date}")
    public FxRate getRxRateByDateBySourceCurrencyAndTargetCurrency(@PathVariable String fromCurrency, @PathVariable String toCurrency, @PathVariable String date) {
        return fxRateService.findRxRateByDateBySourceCurrencyAndTargetCurrency(date, fromCurrency, toCurrency);
    }

}
