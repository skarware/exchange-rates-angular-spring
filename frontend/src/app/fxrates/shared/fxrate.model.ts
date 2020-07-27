import { CurrencyModel } from "./currency.model";

export class CurrencyExchange {
  id: number;
  sourceCurrency: CurrencyModel;
  targetCurrency: CurrencyModel;
  exchangeRate: string;
  effectiveDate: string;
}
