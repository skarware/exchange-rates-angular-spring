package io.github.skarware.exchangerates.controller;

import io.github.skarware.exchangerates.model.CurrencyModel;
import io.github.skarware.exchangerates.service.CurrencyModelService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class CurrencyModelController {

    private final CurrencyModelService currencyModelService;

    public CurrencyModelController(CurrencyModelService currencyModelService) {
        this.currencyModelService = currencyModelService;
    }

    // Get all currencies from database ordered alphabetically
    @GetMapping("/api/currencies")
    public Collection<CurrencyModel> getAllCurrencies() {
        return currencyModelService.getAllByOrderByAlphabeticCode();
    }

    // Get currency by alphabetic code from database
    @GetMapping("/api/currencies/{alphabeticCode}")
    public CurrencyModel getCurrency(@PathVariable String alphabeticCode) {
        return currencyModelService.findByAlphabeticCode(alphabeticCode);
    }

}
