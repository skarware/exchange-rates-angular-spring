package io.github.skarware.exchangerates.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "fx_rates",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"source_currency", "target_currency", "effective_date"})
)
@SequenceGenerator(name = "fx_rates_seq_gen", sequenceName = "fx_rates_seq", initialValue = 10, allocationSize = 1)
public class FxRate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fx_rates_seq_gen")
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "source_currency", referencedColumnName = "numeric_code", nullable = false)
    private CurrencyModel sourceCurrency;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "target_currency", referencedColumnName = "numeric_code", nullable = false)
    private CurrencyModel targetCurrency;

    @Column(name = "exchange_rate", columnDefinition = "DECIMAL(20,10) UNSIGNED", nullable = false)
    private BigDecimal exchangeRate;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @Column(name = "effective_date", columnDefinition = "DATE", nullable = false)
    private Date effectiveDate;

    public FxRate(CurrencyModel sourceCurrency, CurrencyModel targetCurrency, BigDecimal exchangeRate) {
        this.sourceCurrency = sourceCurrency;
        this.targetCurrency = targetCurrency;
        this.exchangeRate = exchangeRate;
    }

    public FxRate() {
    }

}
