package io.github.skarware.exchangerates.service;

import io.github.skarware.exchangerates.dto.CurrencyExchangeDTO;
import io.github.skarware.exchangerates.model.CurrencyModel;
import io.github.skarware.exchangerates.model.FxRate;
import io.github.skarware.exchangerates.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.NoSuchElementException;

@Service
public class CurrencyExchangeService {

    // Rounding mode and scale for final calculation results
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int SCALE = 18;

    // MathContext for intermediate calculations
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FxRateService fxRateService;

    @Value("${fxrates.base.currency}")
    private String BASE_CURRENCY;

    @Value("${fxrates.fetch.api.uri}")
    private String FX_RATES_API_URI;

    @Autowired
    public CurrencyExchangeService(FxRateService fxRateService) {
        this.fxRateService = fxRateService;
    }

    // Pre-convert method for parsing Strings, making sure input currencies are valid
    public CurrencyExchangeDTO getConvertedAmount(String amount, String fromCurrency, String toCurrency, String commissionRateStr) {
        // String parsing in progress, catch NumberFormatException if any
        try {
            // Parse bdAmount String as BigDecimal
            BigDecimal bdAmount = new BigDecimal(NumberUtils.toDotDecimalSeparator(amount));

            // Get commissionRateStr as integer
            BigDecimal commissionRate = new BigDecimal(NumberUtils.toDotDecimalSeparator(commissionRateStr));

            // Convert currencies alphabetic codes to upper case
            String fromCurrencyUpperCase = fromCurrency.toUpperCase();
            String toCurrencyUpperCase = toCurrency.toUpperCase();

            // Calc conversion rate then calc the converted given amount from source currency to target currency
            BigDecimal conversionRatio = calcConversionRatio(fromCurrencyUpperCase, toCurrencyUpperCase);
            BigDecimal convertedAmount = calcConvertedAmount(bdAmount, conversionRatio);

            // If there was no errors in procedure then convertedAmount should be not null but if there was any fault play then throw IllegalArgumentException
            if (convertedAmount != null) {
                // Usually there is a commission fee on currency exchange, fortunately/sorrowful for this task's imaginary user/banker it is 0
                BigDecimal exchangeFee = calcExchangeFee(convertedAmount, commissionRate);

                // Format the result to fixed 2 decimal places
                DecimalFormat format2FixedDecimalPlaces = new DecimalFormat("0.00");
                DecimalFormat formatUpTo5DecimalPlaces = new DecimalFormat("0.#####");
                format2FixedDecimalPlaces.setRoundingMode(ROUNDING_MODE);

                // Return CurrencyExchangeDTO to a controller who will return object in JSON format
                return new CurrencyExchangeDTO(
                        format2FixedDecimalPlaces.format(bdAmount),
                        fromCurrencyUpperCase,
                        toCurrencyUpperCase,
                        formatUpTo5DecimalPlaces.format(conversionRatio),
                        format2FixedDecimalPlaces.format(convertedAmount),
                        formatUpTo5DecimalPlaces.format(commissionRate),
                        format2FixedDecimalPlaces.format(exchangeFee),
                        FX_RATES_API_URI
                );
            } else {
                // TODO: custom API error exception
                throw new IllegalArgumentException("Wrong input data");
            }
        } catch (NumberFormatException exception) {
            logger.warn("Caught NumberFormatException while trying to parse String type into number: {}", exception.getMessage());
            // If any errors return null as a result for invalid input values
            return null;
        }
    }

    // If passed currency is base currency don't look for FxRate in database but create on the fly with exchangeRate of 1
    private FxRate getOrMakeFxRate(String currency) {
        if (currency.equals(BASE_CURRENCY)) {
            logger.info("Creating base Currency on the fly, because given currency ({}) == base currency ({})", currency, BASE_CURRENCY);
            return new FxRate(new CurrencyModel("EUR"), new CurrencyModel("EUR"), BigDecimal.ONE, fxRateService.getLatestDataDate());
        } else {
            // Else get FxRate by target currency from database
            return fxRateService.getLatestByTargetCurrency(currency);
        }
    }

    private BigDecimal calcConversionRatio(String fromCurrency, String toCurrency) {
        // Try to get or make FxRates from given Currency strings
        try {
            // Initialize FxRates for fromCurrency and toCurrency
            FxRate from = getOrMakeFxRate(fromCurrency);
            FxRate to = getOrMakeFxRate(toCurrency);

            // If from and to FxRates have different source then cancel all operations and return null to avoid miscalculations because of base currency mismatch
            if (from.getSourceCurrency().getNumericCode() != to.getSourceCurrency().getNumericCode()) {
                logger.warn("Currency converting failed as FxRates have no common base currency: {} != {}", from.getSourceCurrency(), to.getSourceCurrency());
                return null;
            }

            // If from and to FxRates have different effective dates then cancel all operations and return null to avoid miscalculations
            if (!from.getEffectiveDate().equals(to.getEffectiveDate())) {
                logger.warn("Currency converting failed as FxRates have no common effective date: {} != {}", from.getEffectiveDate(), to.getEffectiveDate());
                return null;
            }

            // Get exchange rates for currencies
            BigDecimal fromRate = from.getExchangeRate();
            BigDecimal toRate = to.getExchangeRate();

            // Calculate Ratio and return it
            return toRate.divide(fromRate, MATH_CONTEXT);

        } catch (NoSuchElementException exc) {
            // TODO: make and throw customized exceptions
            logger.warn("Currency converting failed fromCurrency: {}, toCurrency: {}, error msg: {}", fromCurrency, toCurrency, exc.getMessage());
        }
        // If this point is reached then return null as an error
        return null;
    }

    public static BigDecimal calcConvertedAmount(BigDecimal amount, BigDecimal conversionRatio) {
        // If conversionRatio is null return null too as an error
        if (conversionRatio != null) {
            return amount.multiply(conversionRatio, MATH_CONTEXT);
        }
        return null;
    }

    public static BigDecimal calcExchangeFee(BigDecimal amount, BigDecimal commissionRate) {
        return amount.multiply(commissionRate.divide(BigDecimal.valueOf(100), MATH_CONTEXT), MATH_CONTEXT);
    }
}
