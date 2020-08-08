import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { CurrencyExchangeDTO } from "./currency-exchange-dto.model";
import { CurrencyModel } from "./currency.model";

const BACKEND_HOST_URL = 'http://localhost:8080';
const API_EXCHANGE_PATH = '/api/exchange';
const API_CURRENCIES_PATH = '/api/currencies';

@Injectable({
  providedIn: 'root'
})
export class CalcExchangeService {

  // Inject HttpClient
  constructor(private http: HttpClient) {
  }

  // Fetch available currencies from database through API
  fetchCurrencies(): Observable<CurrencyModel[]> {
    // Return an Observable (kind of promise)
    return this.http.get<CurrencyModel[]>(BACKEND_HOST_URL + API_CURRENCIES_PATH);
  }

  // Get currency conversion result from API
  convertCurrency(fromCurrency: string, toCurrency: string, amount: string, commissionRate: string): Observable<CurrencyExchangeDTO> {
    // Return an Observable (kind of promise)
    return this.http.get<CurrencyExchangeDTO>(BACKEND_HOST_URL + API_EXCHANGE_PATH + '/' + fromCurrency + '/' + toCurrency + '/' + amount + '/' + commissionRate);
  }

}
