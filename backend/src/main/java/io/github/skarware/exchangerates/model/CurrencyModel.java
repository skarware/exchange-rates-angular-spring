package io.github.skarware.exchangerates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private int numericCode;

    @Column(name = "alphabetic_code", columnDefinition = "CHAR(3)", nullable = false)
    // ISO 4217 alphabetic code of this currency
    private String alphabeticCode;

    public CurrencyModel(String alphabeticCode) {
        // Get and assign ISO 4217 numeric code for given currency code. It is probably safe to do this as it is java.utils class
        int numericCode = java.util.Currency.getInstance(alphabeticCode).getNumericCode();
        if (numericCode > 0) {
            this.numericCode = numericCode;
            this.alphabeticCode = alphabeticCode;
        } else {
            throw new IllegalArgumentException(String.valueOf(numericCode));
        }
    }

    protected CurrencyModel() {
        // Record from database to Entity conversion default no-args constructor used, mark those as not new
        this.isNew = false;
    }

    // Get display name on the fly
    public String getDisplayName() {
        return java.util.Currency.getInstance(this.alphabeticCode).getDisplayName();
    }

    // Get symbol on the fly
    public String getSymbol() {
        return java.util.Currency.getInstance(this.alphabeticCode).getSymbol();
    }

    @JsonIgnore
    @Override
    public Integer getId() {
        return numericCode;
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return isNew;
    }

}