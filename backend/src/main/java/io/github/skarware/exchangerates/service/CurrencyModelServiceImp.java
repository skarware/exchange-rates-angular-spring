package io.github.skarware.exchangerates.service;

import io.github.skarware.exchangerates.model.CurrencyModel;
import io.github.skarware.exchangerates.repository.CurrencyModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Currency;

@Service
public class CurrencyModelServiceImp implements CurrencyModelService {

    private final CurrencyModelRepository currencyModelRepository;

    @Autowired
    public CurrencyModelServiceImp(CurrencyModelRepository currencyModelRepository) {
        this.currencyModelRepository = currencyModelRepository;
    }

    @Override
    public CurrencyModel findByNumericCode(int numericCode) {
        return currencyModelRepository.findById(numericCode)
                .orElseThrow(() -> new EntityNotFoundException(String.valueOf(numericCode)));
    }

    @Override
    public CurrencyModel findByAlphabeticCode(String alphabeticCode) {
        // Check if alphabeticCode is valid ISO 4217 currency code
        boolean isAlphabeticCodeValid = Currency.getAvailableCurrencies().stream().anyMatch(el -> el.getCurrencyCode().equals(alphabeticCode));

        if (isAlphabeticCodeValid) {
            return currencyModelRepository.findById(Currency.getInstance(alphabeticCode).getNumericCode())
                    .orElseThrow(() -> new EntityNotFoundException(alphabeticCode));
        } else {
            throw new IllegalArgumentException(String.valueOf(alphabeticCode));
        }
    }

    public CurrencyModel getOrMakeCurrencyModel(String currencyCode) {
        String exceptionMsg;
        // Check if currency already exists in database, if not then instantiate new and return one
        try {
            return findByAlphabeticCode(currencyCode);
        } catch (EntityNotFoundException ignored) {
            // If currency not present in database try create the new and return
            try {
                return new CurrencyModel(currencyCode);
            } catch (IllegalArgumentException exception) {
                exceptionMsg = exception.getMessage();
            }
        } catch (IllegalArgumentException exception) {
            exceptionMsg = exception.getMessage();
        }
        // On invalid input data return the null
        System.err.println("Currency code '" + exceptionMsg + "' is not valid ISO 4217 currency code.");
        return null;
    }

    @Override
    public Collection<CurrencyModel> getAllByOrderByAlphabeticCode() {
        return currencyModelRepository.getAllByOrderByAlphabeticCode();
    }

    @Override
    public CurrencyModel save(CurrencyModel currencyModel) {
        // Before saving check if currency is new or already exists in database
        if (currencyModel.isNew()) {
            // Save and instantly flush from memory to avoid race conditions
            return currencyModelRepository.saveAndFlush(currencyModel);
        }
        return currencyModel;
    }

    @Override
    public CurrencyModel addCurrencyModel(String currencyCode) {
        // Check if source and target Currency Codes already exists in database, if not then make
        CurrencyModel currencyModel = this.getOrMakeCurrencyModel(currencyCode);

        // If not null data returned save it else abort save operation and return null
        if (currencyModel != null) {
            // Save and return CurrencyModel
            return this.save(currencyModel);
        }
        return null;
    }

    @Override
    public boolean exists(CurrencyModel currencyModelToProbe) {
        return currencyModelRepository.exists(Example.of(currencyModelToProbe));
    }

    @Override
    public boolean existsByMatchingNumericCode(CurrencyModel currencyModel) {
        return currencyModelRepository.existsByNumericCode(currencyModel.getNumericCode());
    }

    @Override
    public boolean existsByMatchingAlphabeticCode(CurrencyModel currencyModel) {
        return currencyModelRepository.existsByAlphabeticCode(currencyModel.getAlphabeticCode());
    }

}
