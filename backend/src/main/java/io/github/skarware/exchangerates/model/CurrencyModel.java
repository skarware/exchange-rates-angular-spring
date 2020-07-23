package io.github.skarware.exchangerates.model;

import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

/*
 * Could used java.util.Currency to persist currency in database as String and avoid this class but in the requirements indicated to use
 * H2 relational database so to normalize the data into normal forms i need to create custom Currency model to annotate as the JPA Entity.
 */

@Data
@Entity
@Table(name = "currencies")
public class CurrencyModel implements Persistable<Integer> {

    private static final String DEFAULT_CURRENCY = "EUR";

    @Transient
    private boolean isNew = true;

    @Id
    @Column(name = "numeric_code", columnDefinition = "SMALLINT", unique = true)
    // ISO 4217 numeric code of this currency will act as unique id
    private final int numericCode;

    @Column(name = "alphabetic_code", columnDefinition = "CHAR(3)", nullable = false)
    // ISO 4217 alphabetic code of this currency
    private final String alphabeticCode;

    public CurrencyModel(String alphabeticCode) {
        this.alphabeticCode = alphabeticCode;
        // Get and assign ISO 4217 numeric code for given currency code. It is probably safe to do this as it is java.utils class
        int numericCode = java.util.Currency.getInstance(alphabeticCode).getNumericCode();
        if (numericCode > 0) {
            this.numericCode = numericCode;
        } else {
            throw new IllegalArgumentException("Currency Numeric Code not found: " + numericCode);
        }
    }

    // To keep Entity fields immutable set up DEFAULT_CURRENCY currency code (EUR), avoid using it
    protected CurrencyModel() {
        this(DEFAULT_CURRENCY);
        // Record from database to Entity conversion default no-args constructor used, mark those as not new
        this.isNew = false;
    }

    @Override
    public Integer getId() {
        return numericCode;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

}