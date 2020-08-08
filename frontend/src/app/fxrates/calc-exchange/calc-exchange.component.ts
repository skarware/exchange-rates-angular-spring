import { Component, OnInit } from '@angular/core';
import { NgForm } from "@angular/forms";
import { CurrencyExchangeDTO } from "../shared/currency-exchange-dto.model";
import { CalcExchangeService } from "../shared/calc-exchange.service";
import { CurrencyModel } from "../shared/currency.model";

@Component({
  selector: 'app-calc-exchange',
  templateUrl: './calc-exchange.component.html',
  styleUrls: [ './calc-exchange.component.css' ]
})
export class CalcExchangeComponent implements OnInit {
  // Define Material progress-bar/spinner display/hide booleans
  public isLoading: boolean;
  public isConverting: boolean;

  // Initialize currencyExchangeDTO for incoming currency conversion data
  currencyExchangeDTO: CurrencyExchangeDTO;

  // "EUR", "USD", "GBP", ...
  currencyOptions: CurrencyModel[];

  // Inject calcExchangeService into this component as private class member
  constructor(private calcExchangeService: CalcExchangeService) {
  }

  ngOnInit(): void {

    // While loading data from server
    this.isLoading = true;

    // Fetch available currencies from API
    this.calcExchangeService.fetchCurrencies()
      .subscribe((currencyOptions: CurrencyModel[]) => {

        // After data received from server
        this.isLoading = false;

        // Then currencyOptions Change update the dataSource with new data
        this.currencyOptions = currencyOptions;

      });
  }

  onAmountInput(formElement: NgForm): void {
    // Check if form input data valid
    if (formElement.invalid) {
      return;
    }

    // True, while fetching conversion result from server
    this.isConverting = true;

    const amount = formElement.value.amount;
    const commissionRate = formElement.value.commissionRate;
    const fromCurrency = formElement.value.fromCurrency.alphabeticCode;
    const toCurrency = formElement.value.toCurrency.alphabeticCode;

    // Get currency conversion result from API
    this.calcExchangeService.convertCurrency(fromCurrency, toCurrency, amount, commissionRate)
      .subscribe((newData: CurrencyExchangeDTO) => {

        // False, after conversion result received from server
        this.isConverting = false;

        // Update the currencyExchangeDTO with new data
        this.currencyExchangeDTO = newData;

      });
  }
}


