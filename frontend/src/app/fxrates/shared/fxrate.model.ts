import { CurrencyModel } from "./currency.model";

export class FxRate {
  id: number;
  sourceCurrency: CurrencyModel;
  targetCurrency: CurrencyModel;
  exchangeRate: number;
  effectiveDate: string;
}
