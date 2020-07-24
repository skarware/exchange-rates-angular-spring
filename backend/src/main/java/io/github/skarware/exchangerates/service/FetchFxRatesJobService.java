package io.github.skarware.exchangerates.service;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.skarware.exchangerates.dto.FxRatesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

import static java.math.MathContext.DECIMAL64;

@Service
public class FetchFxRatesJobService {

    private final URI FX_RATES_URI = new URI("http://www.lb.lt/webservices/FxRates/FxRates.asmx/getCurrentFxRates?tp=LT");

    private final FxRateService fxRateService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public FetchFxRatesJobService(FxRateService fxRateService) throws URISyntaxException {
        this.fxRateService = fxRateService;
    }

    public void executeFetchAddFxRates() {
        logger.info("The job has begun...");

        try {
            // Fetch FxRates from API
            FxRatesDTO fxRatesDTO = fetchFxRates(FX_RATES_URI);

            // Remap data and add into database
            addFxRates(fxRatesDTO);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Job has finished...");
    }

    public FxRatesDTO fetchFxRates(URI URI) throws IOException, InterruptedException {

        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(module);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI)
                .version(HttpClient.Version.HTTP_2)
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        // Check if response was successful
        if (response.statusCode() == 200) {
            logger.info("response.statusCode() " + response.statusCode());
            return xmlMapper.readValue(response.body(), FxRatesDTO.class);
        } else {
            logger.warn("Failed to fetch FxRates data from " + URI.toURL() + ", status code: " + response.statusCode());
        }
        return null;
    }

    private void addFxRates(FxRatesDTO fxRatesDTO) {
        if (fxRatesDTO == null) {
            return;
        }
        // Map fxRates DTO into Entity before saving into database
        fxRatesDTO.getFxRates().forEach(el -> {
            String sourceCurrency = el.getCcyAmt().get(0).getCcy();
            BigDecimal sourceAmt = el.getCcyAmt().get(0).getAmt();
            String targetCurrency = el.getCcyAmt().get(1).getCcy();
            BigDecimal targetAmt = el.getCcyAmt().get(1).getAmt();
            BigDecimal exchangeRate = targetAmt.divide(sourceAmt, DECIMAL64);
            Date effectiveDate = el.getDt();
            fxRateService.addFxRate(sourceCurrency, targetCurrency, exchangeRate, effectiveDate);
        });
    }
}
