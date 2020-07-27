import { MatTableDataSource } from '@angular/material/table';
import { _isNumberValue } from '@angular/cdk/coercion';
import { CurrencyExchange } from './fxrate.model';

// Custom nested objects DataSource class for Material table
class NestedObjectsDataSource<T> extends MatTableDataSource<T> {
  // Angular Material 2+ DataTable Sorting with nested objects
  sortingDataAccessor: (data: T, sortHeaderId: string) => string | number = (
    data: T,
    sortHeaderId: string
  ): string | number => {
    let value = null;
    if (sortHeaderId.indexOf('.') !== -1) {
      const ids = sortHeaderId.split('.');
      value = data[ids[0]][ids[1]];
    } else {
      value = data[sortHeaderId];
    }
    return _isNumberValue(value) ? Number(value) : value;
  };

  // Angular Material 2+ DataSource filter with nested object
  filterPredicate: (data: T, filter: string) => boolean = (
    data: T,
    filter: string
  ): boolean => {
    const accumulator = (currentTerm: string, key: string) => {
      return this.nestedFilterCheck(currentTerm, data, key);
    };
    const dataStr = Object.keys(data).reduce(accumulator, '').toLowerCase();
    // Transform the filter by converting it to lowercase and removing whitespace.
    const transformedFilter = filter.trim().toLowerCase();
    return dataStr.indexOf(transformedFilter) !== -1;
  };

  nestedFilterCheck(search: string, data: T, key: string) {
    if (typeof data[key] === 'object') {
      for (const k in data[key]) {
        if (data[key][k] !== null) {
          search = this.nestedFilterCheck(search, data[key], k);
        }
      }
    } else {
      search += data[key];
    }
    return search;
  }

  constructor(initialData: T[]) {
    super(initialData);
  }
}

// Specific FxRate implementation of NestedObjectsDataSource
export class FxRateMatTableDataSource extends NestedObjectsDataSource<CurrencyExchange> {
  constructor(initialData: CurrencyExchange[]) {
    super(initialData);
  }
}
