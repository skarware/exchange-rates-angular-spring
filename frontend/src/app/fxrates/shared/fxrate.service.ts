import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { FxRate } from './fxrate.model';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

const BACKEND_HOST_URL = 'http://localhost:8080';
const API_PATH = '/api/fxrates';

@Injectable({
  providedIn: 'root'
})
export class FxRateService implements OnDestroy {

    // Inject HttpClient
    constructor(private http: HttpClient, private router: Router) {
    }

    // Initialize fxRates array as empty before fetching data from dataSource
    private fxRates: FxRate[] = [];

    // Initialize Observable to multicast changes on this.fxRates data
    private fxRatesChanges: Subject<FxRate[]> = new Subject<FxRate[]>();

    // Fetch FxRate data from database through API
    fetchFxRates(): void {
      // Get fxRates data from API
      this.http.get<FxRate[]>(BACKEND_HOST_URL + API_PATH)
        .subscribe((response) => {
          // Assign new data to fxRates array; and multicast it to fxRatesChanges observers/subscribers
          this.fxRates = response;
          this.fxRatesChanges.next([ ...this.fxRates ]);
        });
    }

    // Return Observable for Observers to subscribe for new this.fxRates data
    getFxRatesChanges(): Observable<FxRate[]> {
      return this.fxRatesChanges.asObservable();
    }

  ngOnDestroy(): void {
    // To avoid memory leaks set as complete if this component is destroyed
    this.fxRatesChanges.complete();
  }
}
