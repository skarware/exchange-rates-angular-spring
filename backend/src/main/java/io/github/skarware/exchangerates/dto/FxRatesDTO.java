package io.github.skarware.exchangerates.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class FxRatesDTO {
    @JsonProperty("FxRate")
    private List<FxRateDTO> fxRates;

    @Data
    public static class FxRateDTO {
        @JsonProperty("Tp")
        private String tp;
        @JsonProperty("Dt")
        private Date dt;
        @JacksonXmlElementWrapper(useWrapping = false)
        private ArrayList<FxRateDTO.CcyAmt> CcyAmt;

        @Data
        public static class CcyAmt {
            @JsonProperty("Ccy")
            private String ccy;
            @JsonProperty("Amt")
            private BigDecimal amt;
        }
    }
}

