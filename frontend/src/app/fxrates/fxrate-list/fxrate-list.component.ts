import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';

import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Subscription } from 'rxjs';

import { FxRate } from '../shared/fxrate.model';
import { FxRateService } from '../shared/fxrate.service';
import { FxRateMatTableDataSource } from '../shared/fxrate-table-datasource';

@Component({
  selector: 'app-fxrate-list',
  templateUrl: './fxrate-list.component.html',
  styleUrls: ['./fxrate-list.component.css'],
})
export class FxrateListComponent implements OnInit, OnDestroy {
  // Define Material progress-bar display/hide boolean
  public isLoading: boolean;

  // Initialize as empty subscription then inside constructor block subscribe to fxRate data changes
  private fxRatesChangeSubscription: Subscription = Subscription.EMPTY;

  // Initialize columns to render on Material table
  displayedColumns: string[] = [
    'sourceCurrency.alphabeticCode',
    'targetCurrency.alphabeticCode',
    'exchangeRate',
    'effectiveDate',
  ];

  // Define custom FxRate nested objects DataSource for Material table
  dataSource: FxRateMatTableDataSource;

  // Define Paginator and Sort functionality to Material table
  private paginator: MatPaginator;
  private sort: MatSort;

  @ViewChild(MatSort, { static: false }) set matSort(ms: MatSort) {
    this.sort = ms;
    if (this.sort) {
      this.dataSource.sort = this.sort;
    }
  }

  @ViewChild(MatPaginator, { static: false }) set matPaginator(
    mp: MatPaginator
  ) {
    this.paginator = mp;
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }
  }

  // Inject FxRateService into this component as private class member
  constructor(private fxRateService: FxRateService) {}

  ngOnInit(): void {
    this.isLoading = true;
    // Fetch FxRates data from API
    this.fxRateService.fetchFxRates();
    // Subscribe to the fxRatesChange EventEmitter and listen for new data
    this.fxRatesChangeSubscription = this.fxRateService
      .getFxRatesChanges()
      .subscribe((newFxRatesData: FxRate[]) => {
        this.isLoading = false;
        // Then fxRates Change update the dataSource with new data
        this.updateFxRateDataSource(newFxRatesData);
      });
  }

  ngOnDestroy(): void {
    // To avoid memory leaks unsubscribe if this component is destroyed
    this.fxRatesChangeSubscription.unsubscribe();
  }

  // Fn to update Material table dataSource and its attributes
  private updateFxRateDataSource(fxRates: FxRate[]): void {
    this.dataSource = new FxRateMatTableDataSource(fxRates);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
}
