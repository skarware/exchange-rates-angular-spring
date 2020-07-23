package io.github.skarware.exchangerates.service;

import io.github.skarware.exchangerates.model.CurrencyModel;
import io.github.skarware.exchangerates.model.FxRate;
import io.github.skarware.exchangerates.repository.FxRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

@Service
public class FxRateServiceImp implements FxRateService {

    private final FxRateRepository fxRateRepository;
    private final CurrencyModelService currencyModelService;

    @Autowired
    public FxRateServiceImp(FxRateRepository fxRateRepository, CurrencyModelService currencyModelService) {
        this.fxRateRepository = fxRateRepository;
        this.currencyModelService = currencyModelService;
    }

    @Override
    public FxRate findById(Long id) {
        return fxRateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(id)));
    }

    @Override
    public Collection<FxRate> getAllByOrderByEffectiveDate() {
        return fxRateRepository.getAllByOrderByEffectiveDate();
    }

    @Override
    public Collection<FxRate> getAllByEffectiveDateGreaterThanEqualOrderByEffectiveDateDesc(Date date) {
        return fxRateRepository.getAllByEffectiveDateGreaterThanEqualOrderByEffectiveDateDesc(date);
    }

    // TODO: Should be possible to merge existing CurrencyModel obj with new FxRate obj before INSERT. Would save two SELECTS!
    @Override
    public FxRate save(FxRate fxRate) {
        // Saving cascade for child CurrencyModel elements is not set, instead CurrencyModel save logic lays in it service implementation
        currencyModelService.save(fxRate.getSourceCurrency());
        currencyModelService.save(fxRate.getTargetCurrency());

        // Save entity into database letting ORM to decide then flush it from memory into database
        return fxRateRepository.save(fxRate);
    }

    // TODO: implement saveAll and addFxRates versions, should be more efficient then multiple entity data fetched from API

    @Override
    public FxRate addFxRate(String sourceCurrencyCode, String targetCurrencyCode, String exchangeRate) {
        // Check if source and target Currency Codes already exists in database, if not then make
        CurrencyModel sourceCurrencyModel = currencyModelService.getOrMakeCurrencyModel(sourceCurrencyCode);
        CurrencyModel targetCurrencyModel = currencyModelService.getOrMakeCurrencyModel(targetCurrencyCode);

        // Create new FxRate with given data
        FxRate fxRate = new FxRate(sourceCurrencyModel, targetCurrencyModel, new BigDecimal(5));

        // Save and return new FxRate
        return this.save(fxRate);
    }


}