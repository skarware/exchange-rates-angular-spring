import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';

import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Subscription } from 'rxjs';

import { FxRate } from '../shared/fxrate.model';
import { FxRateService } from '../shared/fxrate.service';

@Component({
  selector: 'app-fxrate-list',
  templateUrl: './fxrate-list.component.html',
  styleUrls: ['./fxrate-list.component.css'],
})
export class FxrateListComponent implements OnInit, OnDestroy {
  // Initialize as empty array
  private fxRates: FxRate[] = [];
  // Initialize as empty subscription then inside constructor block subscribe to fxRate data changes
  private fxRatesChangeSubscription: Subscription = Subscription.EMPTY;

  // Initialize columns to render on Material table
  displayedColumns: string[] = [
    'sourceCurrency',
    'targetCurrency',
    'exchangeRate',
    'effectiveDate',
  ];
  // displayedColumns: string[] = ['position', 'name', 'weight', 'symbol'];

  // Define dataSource for Material table
  dataSource: MatTableDataSource<FxRate>;

  // Define Paginator and Sort functionality to Material table
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  public isLoading: boolean;

  // Inject FxRateService into this component as private class member
  constructor(private fxRateService: FxRateService) {}
  // Function to update Material table dataSource
  private updateMatTableDataSource(fxRates: FxRate[]): void {
    this.dataSource = new MatTableDataSource<FxRate>(fxRates);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  // Initialization
  ngOnInit(): void {
    this.isLoading = true;
    // Fetch FxRates data from API
    this.fxRateService.fetchFxRates();
    // Subscribe to the fxRatesChange EventEmitter and listen for new data
    this.fxRatesChangeSubscription = this.fxRateService
      .getFxRatesChanges()
      .subscribe((newFxRatesData: FxRate[]) => {
        this.isLoading = false;
        // Then fxRatesChange update the fxRates with new data
        this.fxRates = newFxRatesData;
        this.updateMatTableDataSource(this.fxRates);
      });
  }

  ngOnDestroy(): void {
    // To avoid memory leaks unsubscribe if this component is destroyed
    this.fxRatesChangeSubscription.unsubscribe();
  }
}
