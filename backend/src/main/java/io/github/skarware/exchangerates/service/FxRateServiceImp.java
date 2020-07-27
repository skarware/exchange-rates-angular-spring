package io.github.skarware.exchangerates.service;

import io.github.skarware.exchangerates.model.CurrencyModel;
import io.github.skarware.exchangerates.model.FxRate;
import io.github.skarware.exchangerates.repository.FxRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class FxRateServiceImp implements FxRateService {

    private final FxRateRepository fxRateRepository;
    private final CurrencyModelService currencyModelService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
    public FxRate getByDateBySourceCurrencyAndTargetCurrency(String date, String fromCurrency, String toCurrency) {
        CurrencyModel sourceCurrency = currencyModelService.getOrMake(fromCurrency);
        CurrencyModel targetCurrency = currencyModelService.getOrMake(toCurrency);
        Date dateObj = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            dateObj = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fxRateRepository.findByEffectiveDateAndSourceCurrencyAndTargetCurrency(dateObj, sourceCurrency, targetCurrency);
    }

    @Override
    public Collection<FxRate> getLatest100BySourceCurrencyAndTargetCurrency(String fromCurrency, String toCurrency) {
        CurrencyModel sourceCurrency = currencyModelService.getOrMake(fromCurrency);
        CurrencyModel targetCurrency = currencyModelService.getOrMake(toCurrency);
        return fxRateRepository.findFirst100BySourceCurrencyAndTargetCurrencyOrderByIdDesc(sourceCurrency, targetCurrency);
    }

    @Override
    public Collection<FxRate> getLatestFxRates() {
        Date latestDate = getLatestDataDate();
        return fxRateRepository.findByEffectiveDate(latestDate);
    }

    @Override
    public Date getLatestDataDate() {
        return fxRateRepository.selectDateFromFxRatesLimitOneOrderByIdDesc();
    }

    @Override
    public FxRate getLatestByTargetCurrency(String targetCurrency) {
        int numericCode = currencyModelService.findByAlphabeticCode(targetCurrency).getNumericCode();
        FxRate fxRate = fxRateRepository.selectFxRateByTargetCurrencyNumericCodeLimitOneOrderByIdDesc(numericCode);
        if (fxRate != null) {
            return fxRate;
        } else {
            throw new NoSuchElementException("FxRate by given target: '" + targetCurrency.toUpperCase() + "' not found.");
        }
    }

    // TODO: Should be possible to merge existing CurrencyModel obj with new FxRate obj before INSERT. Would eliminate two SELECTS!

    @Override
    public FxRate save(FxRate fxRate) {
        // Saving cascade for child CurrencyModel elements is not set, instead CurrencyModel save logic lays in it service implementation
        currencyModelService.save(fxRate.getSourceCurrency());
        currencyModelService.save(fxRate.getTargetCurrency());

        logger.debug("Saving fxRate model into database: {}", fxRate);
        // Save entity into database letting ORM to decide then flush it from memory into database
        return fxRateRepository.save(fxRate);
    }

    // TODO: implement saveAll and addFxRates versions, should be more efficient then multiple entity data fetched from API

    @Override
    public FxRate add(String sourceCurrencyCode, String targetCurrencyCode, BigDecimal exchangeRate, Date effectiveDate) {
        // Check if source and target Currency Codes already exists in database, if not then make
        CurrencyModel sourceCurrencyModel = currencyModelService.getOrMake(sourceCurrencyCode);
        CurrencyModel targetCurrencyModel = currencyModelService.getOrMake(targetCurrencyCode);

        // If valid data returned proceed with saving it else abort save operation and return null
        if (sourceCurrencyModel != null && targetCurrencyModel != null) {
            // Create new FxRate with given data
            FxRate fxRate = new FxRate(
                    sourceCurrencyModel,
                    targetCurrencyModel,
                    exchangeRate,
                    effectiveDate
            );

            // Save and return new FxRate
            return this.save(fxRate);
        }
        logger.debug("FxRate not saved in database");

        return null;
    }


}
