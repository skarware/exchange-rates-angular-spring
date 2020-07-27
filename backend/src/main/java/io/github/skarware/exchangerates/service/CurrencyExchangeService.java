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
            int commissionRate = Integer.parseInt(commissionRateStr);

            // Convert currencies alphabetic codes to upper case
            String fromCurrencyUpperCase = fromCurrency.toUpperCase();
            String toCurrencyUpperCase = toCurrency.toUpperCase();

            // Convert given amount from source currency to target currency
            BigDecimal convertedAmount = convertCurrency(bdAmount, fromCurrencyUpperCase, toCurrencyUpperCase);

            if (convertedAmount != null) {
                // Usually there is a commission fee on currency exchange, fortunately/sorrowful for this task's imaginary user/banker it is 0
                BigDecimal exchangeFee = calcExchangeFee(convertedAmount, commissionRate);

                // Format the result up to 18 decimal places hiding more than 2 zeros
                DecimalFormat df = new DecimalFormat("0.00");
//                DecimalFormat df = new DecimalFormat();
//                df.setMaximumFractionDigits(2);
//                df.setMinimumFractionDigits(2);
                df.setRoundingMode(ROUNDING_MODE);

                // Return CurrencyExchangeDTO to a controller who will return object in JSON format
                return new CurrencyExchangeDTO(
                        df.format(bdAmount),
                        fromCurrencyUpperCase,
                        toCurrencyUpperCase,
                        df.format(convertedAmount),
                        df.format(commissionRate),
                        df.format(exchangeFee),
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

    // To be on the safe side method parameters should accept currency names as strings
    private BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
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

            // Calc conversion rate then calc the converted amount into new currency
            BigDecimal conversionRatio = calcConversionRatio(from, to);
            BigDecimal convertedAmount = calcConvertedAmount(amount, conversionRatio);

            // Round up to SCALE (18) decimal places using ROUNDING_MODE (HALF_UP)
            return convertedAmount.setScale(SCALE, ROUNDING_MODE);

        } catch (NoSuchElementException exc) {
            // TODO: make and throw customized exceptions
            logger.warn("Currency converting failed fromCurrency: {}, toCurrency: {}, error msg: {}", fromCurrency, toCurrency, exc.getMessage());
        }
        // Should not reach this point buf if do then return null as error
        return null;
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

    public static BigDecimal calcConvertedAmount(BigDecimal amount, BigDecimal conversionRatio) {
        return amount.multiply(conversionRatio, MATH_CONTEXT);
    }

    public static BigDecimal calcConversionRatio(FxRate from, FxRate to) {
        BigDecimal fromRate = from.getExchangeRate();
        BigDecimal toRate = to.getExchangeRate();
        // Calculate Ratio and return
        return toRate.divide(fromRate, MATH_CONTEXT);
    }

    public static BigDecimal calcExchangeFee(BigDecimal amount, double commissionRate) {
        /*
          Note that BigDecimal's valueOf method converts double to its String representation before converting to BigDecimal,
          therefor it is safer then using double constructor as some Real numbers, like 0.01, does not have an exact representation
          in double type so the result in those cases will differ from expected.
         */
        return amount.multiply(BigDecimal.valueOf(commissionRate).divide(BigDecimal.valueOf(100), MATH_CONTEXT), MATH_CONTEXT);
    }
}
