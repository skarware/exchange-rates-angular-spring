export class CurrencyExchangeDTO {
  amount: number;
  from: string;
  to: string;
  conversionRatio: number;
  convertedAmount: number;
  commissionRate: number;
  exchangeFee: number;
  fxRatesSource: string;
}
