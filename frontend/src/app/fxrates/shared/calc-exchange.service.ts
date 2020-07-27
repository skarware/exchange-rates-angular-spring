import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { CurrencyExchangeDTO } from "./currency-exchange-dto.model";
import { CurrencyModel } from "./currency.model";

const BACKEND_HOST_URL = 'http://localhost:8080';
const API_EXCHANGE_PATH = '/api/exchange';
const API_CURRENCIES_PATH = '/api/currencies';

@Injectable({
  providedIn: 'root'
})
export class CalcExchangeService implements OnDestroy {

  // Inject HttpClient
  constructor(private http: HttpClient) {
  }

  // Initialize currencyExchangeDTO array as empty before fetching data from dataSource
  private currencyExchangeDTO: CurrencyExchangeDTO;

  // Initialize Observable to multicast changes on this.currencyExchangeDTO data
  private currencyExchangeDTOChanges: Subject<CurrencyExchangeDTO> = new Subject<CurrencyExchangeDTO>();

  // Initialize currencyOptions array as empty before fetching data from dataSource
  private currencyOptions: CurrencyModel[];

  // Initialize Observable to multicast changes on this.currencyOptions data
  private currencyOptionsChanges: Subject<CurrencyModel[]> = new Subject<CurrencyModel[]>();


  // Fetch available currencies from database through API
  fetchCurrencies(): void {
    this.http.get<CurrencyModel[]>(BACKEND_HOST_URL + API_CURRENCIES_PATH)
      .subscribe((response) => {
        // Assign new data to currencyOptions array; and multicast it to currencies observers/subscribers
        this.currencyOptions = response;
        this.currencyOptionsChanges.next([...this.currencyOptions]);
      });
  }

  // Return Observable for Observers to subscribe for new this.currencyOptions data
  getCurrencyOptionsChanges(): Observable<CurrencyModel[]> {
    return this.currencyOptionsChanges.asObservable();
  }

  // Return Observable for Observers to subscribe for new this.currencyExchangeDTO data
  getCurrencyExchangeChanges(): Observable<CurrencyExchangeDTO> {
    return this.currencyExchangeDTOChanges.asObservable();
  }

  convertCurrency(fromCurrency: string, toCurrency: string, amount: string, commissionRate: string): void {
    // Get currency conversion result from API
    this.http.get<CurrencyExchangeDTO>(BACKEND_HOST_URL + API_EXCHANGE_PATH + '/' + fromCurrency + '/' + toCurrency + '/' + amount + '/' + commissionRate)
      .subscribe((response) => {
        // If new data persisted successfully on database only then:
        {
          // Push new data into local plates array
          this.currencyExchangeDTO = response;
          // Multicast new data to currencyExchangeDTOChanges observers/subscribers
          this.currencyExchangeDTOChanges.next(this.currencyExchangeDTO);

        }
      });
  }

  ngOnDestroy(): void {
    // To avoid memory leaks set as complete if this component is destroyed
    this.currencyExchangeDTOChanges.complete();
  }
}
