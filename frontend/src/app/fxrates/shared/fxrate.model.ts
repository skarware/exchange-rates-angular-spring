export class FxRate {
  id: number;
  sourceCurrency: {
    numericCode: number;
    alphabeticCode: string;
  };
  targetCurrency: {
    numericCode: number;
    alphabeticCode: string;
  };
  exchangeRate: string;
  effectiveDate: string;
}
