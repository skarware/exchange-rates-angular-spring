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
        return currencyModelRepository.findById(Currency.getInstance(alphabeticCode).getNumericCode())
                .orElseThrow(() -> new EntityNotFoundException(alphabeticCode));
    }

    public CurrencyModel getOrMakeCurrencyModel(String currencyCode) {
        CurrencyModel currencyModel;
        // Check if currency already exists in database, if not then instantiate new and return one
        try {
            currencyModel = findByAlphabeticCode(currencyCode);
        } catch (EntityNotFoundException exception) {
            System.err.println("Currency '" + exception.getMessage() + "' not found in database. Instantiating for saving new one.");
            try {
                currencyModel = new CurrencyModel(currencyCode);
            } catch (IllegalArgumentException exc) {
                System.err.println("Currency code '" + exc.getMessage() +"' is not valid ISO 4217 currency code.");
                return null;
            }
        }
        return currencyModel;
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
