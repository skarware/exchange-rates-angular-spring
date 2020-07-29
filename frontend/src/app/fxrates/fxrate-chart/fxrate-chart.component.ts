import { Component, OnInit, ViewChild } from '@angular/core';
import { Label } from "ng2-charts";
import { ChartDataSets, ChartOptions } from "chart.js";
import { FxRateService } from "../shared/fxrate.service";
import { ActivatedRoute, ParamMap, Router } from "@angular/router";
import { FxRate } from "../shared/fxrate.model";

const BASE_CURRENCY = 'EUR';

@Component({
  selector: 'app-fxrate-chart',
  templateUrl: './fxrate-chart.component.html',
  styleUrls: ['./fxrate-chart.component.css']
})
export class FxrateChartComponent implements OnInit {
  // Define Material progress-bar display/hide boolean
  public isLoading: boolean;

  // Set up targetCurrency
  private targetCurrency: string;

  public lineChartType = 'line';
  public lineChartOptions: ChartOptions = {
    responsive: true,
    scales: {
      xAxes: [{
        type: 'time',
        time: {
          unit: 'day'
        }
      }],
    },
  };
  public lineChartLegend = true;

  // Initialize Chart data and labels with empty arrays or embrace for errors
  public lineChartLabels: Label[] = null;
  public lineChartData: ChartDataSets[] = [{data: [], label: ''}];

  // Inject FxRateService into this component as private class member
  constructor(
    private fxRateService: FxRateService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.isLoading = true;
    // Look for parameters in URL path
    // this.targetCurrency = this.activatedRoute.snapshot.paramMap.get('currency');
    this.activatedRoute.paramMap
      // Then param change callback function executed
      .subscribe((paramMap: ParamMap) => {
        // Get param for fetching FxRate data
        this.targetCurrency = paramMap.get('currency').toUpperCase();
        // Fetch latest FxRate[] by given target currency string
        this.fxRateService.fetchLatestFxRatesByTargetCurrency(this.targetCurrency)
          .subscribe((response) => {
            if (response != null && response.length > 0) {
              // and push new chart into
              this.addCurveToChart(response);
              this.isLoading = false;
            } else if (this.targetCurrency === BASE_CURRENCY) {
              // TODO: if BASE_CURRENCY given as param show how base currency relate to other currencies
              console.log("Get ready for some action")
            } else {
              // Redirect back to home path
              this.router.navigate(['/'])
                .then(r => console.log("Invalid param given, redirecting back to home page"));
            }
          });
      });
  }

  private addCurveToChart(fxRates: FxRate[]) {
    // Define local labels and chartDataSet
    const labels: Label[] = [];
    const chartDataSet: ChartDataSets = {data: [], label: ''};

    fxRates.forEach((fxRate, index) => {
      // Extract currencies exchange rate and effective date
      const date = fxRate.effectiveDate;
      const rate = fxRate.exchangeRate;

      // Push extracted data from FxRate into new ChartDataSet obj
      chartDataSet.data.push(rate);
      labels.push(date);
    })

    // Set Label for chartDataSet
    chartDataSet.label = BASE_CURRENCY + '/' + this.targetCurrency;

    // If this.lineChartData's first element is empty data arr then overwrite it, else push additional curve to the graph
    if (this.lineChartData[0].data === []) {
      this.lineChartData.push(chartDataSet);
    } else {
      this.lineChartData = [chartDataSet];
    }

    // In any case it should be ok to overwrite labels as it should be the same in theory...
    this.lineChartLabels = labels;
  }
}
