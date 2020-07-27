import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from "@angular/forms";
import { Subscription } from "rxjs";
import { CurrencyExchangeDTO } from "../shared/currency-exchange-dto.model";
import { CalcExchangeService } from "../shared/calc-exchange.service";
import { CurrencyModel } from "../shared/currency.model";

@Component({
  selector: 'app-calc-exchange',
  templateUrl: './calc-exchange.component.html',
  styleUrls: ['./calc-exchange.component.css']
})
export class CalcExchangeComponent implements OnInit, OnDestroy {
  // Define Material progress-bar/spinner display/hide booleans
  public isLoading: boolean;
  public isConverting: boolean;

  // Initialize as empty subscription then inside constructor block subscribe to currencyDTOExchange data changes
  private currencyExchangeDTOChangeSubscription: Subscription = Subscription.EMPTY;

  // Initialize currencyExchangeDTO for incoming currency conversion data
  currencyExchangeDTO: CurrencyExchangeDTO

  // Initialize as empty subscription then inside constructor block subscribe to currencyOptions data changes
  private currencyOptionsChangeSubscription: Subscription = Subscription.EMPTY;

  // currencyOptions = ["USD", "EUR", "GBP", ...];
  currencyOptions: CurrencyModel[];

  // Inject calcExchangeService into this component as private class member
  constructor(private calcExchangeService: CalcExchangeService) {
  }

  ngOnInit(): void {
    this.isLoading = true;
    // Fetch CurrencyExchange[] data from API
    this.calcExchangeService.fetchCurrencies();
    // Subscribe to the currencyOptions Change EventEmitter and listen for new data
    this.currencyOptionsChangeSubscription = this.calcExchangeService.getCurrencyOptionsChanges()
      .subscribe((newData: CurrencyModel[]) => {
        this.isLoading = false;
        // Then currencyOptions Change update the dataSource with new data
        this.currencyOptions = newData;
      });
  }

  ngOnDestroy(): void {
    // To avoid memory leaks unsubscribe if this component is destroyed
    this.currencyExchangeDTOChangeSubscription.unsubscribe();
  }

  onAmountInput(formElement: NgForm): void {
    if (formElement.invalid) {
      return;
    }
    this.isConverting = true;

    const amount = formElement.value.amount;
    const commissionRate = formElement.value.commissionRate;
    const fromCurrency = formElement.value.fromCurrency.alphabeticCode;
    const toCurrency = formElement.value.toCurrency.alphabeticCode;

    this.calcExchangeService.convertCurrency(fromCurrency, toCurrency, amount, commissionRate);

    this.currencyExchangeDTOChangeSubscription = this.calcExchangeService.getCurrencyExchangeChanges()
      .subscribe((newData: CurrencyExchangeDTO) => {
        this.isConverting = false;
        // Then currencyDTOExchange[] Change update the dataSource with new data
        this.currencyExchangeDTO = newData;
      });
  }
}


