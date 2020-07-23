package io.github.skarware.exchangerates.repository;

import io.github.skarware.exchangerates.model.CurrencyModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface CurrencyModelRepository extends JpaRepository<CurrencyModel, Integer> {

    CurrencyModel findByNumericCode(int numericCode);

    Collection<CurrencyModel> getAllByOrderByAlphabeticCode();

    boolean existsByAlphabeticCode(String alphabeticCode);

    boolean existsByNumericCode(int numericCode);

}
