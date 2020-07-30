import { Component, OnInit } from '@angular/core';
import { Label } from "ng2-charts";
import { ChartDataSets, ChartOptions } from "chart.js";
import { FxRateService } from "../shared/fxrate.service";
import { ActivatedRoute, ParamMap } from "@angular/router";
import { FxRate } from "../shared/fxrate.model";

@Component({
  selector: 'app-fxrate-chart',
  templateUrl: './fxrate-chart.component.html',
  styleUrls: ['./fxrate-chart.component.css']
})
export class FxrateChartComponent implements OnInit {
  // Define Material spinner display/hide boolean
  public isLoading: boolean;

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
    elements: {
      line: {
        tension: 0 // disabling bezier curves will improve render times since drawing a straight line is more performant than a bezier curve
      }
    },
    tooltips: {
      intersect: false,
      mode: 'index',
      callbacks: {
        label: function (tooltipItem, myData) {
          let label = myData.datasets[tooltipItem.datasetIndex].label || '';
          if (label) {
            label += ': ';
          }
          label += parseFloat(tooltipItem.value)//.toFixed(4);
          return label;
        }
      }
    }
  };
  public lineChartLegend = true;

  // Define Chart data and labels with empty arrays or embrace for errors
  public lineChartLabels: Label[] = [];
  public lineChartData: ChartDataSets[] = [];

  // Inject FxRateService into this component as private class member
  constructor(
    private fxRateService: FxRateService,
    private activatedRoute: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
    this.isLoading = true;
    // Look for parameters in URL path
    this.activatedRoute.paramMap
      // Then param change callback function executed
      .subscribe((paramMap: ParamMap) => {
        if (paramMap.has('currency')) {

          // If currency param exists set up component into single currency view mode
          // this.isViewModeSingleCurrency = true;

          // Get param for fetching FxRate data
          const targetCurrency = paramMap.get('currency').toUpperCase();
          this.fetchAndAddCurveToChart(targetCurrency);

        } else {
          // TODO: Make non-parameter chart page dynamical, let the use choose from 1 to 3 currencies, how they relate base currency

          // show how base currency relate to other currencies
          this.fetchAndAddCurveToChart('USD');
          this.fetchAndAddCurveToChart('GBP');
          this.fetchAndAddCurveToChart('CAD');

        }
      });
  }

  private fetchAndAddCurveToChart(targetCurrency: string) {
    // Fetch latest FxRate[] by given string of target currency
    this.fxRateService.fetchLatestFxRatesByTargetCurrency(targetCurrency)
      .subscribe((response) => {
        // Given wrong parameter returned array may be empty so need to check
        if (response != null && response.length > 0) {
          // Add a curve into the chart
          this.addCurveToChart(response);
          // After first curve added to the chart loading icon can be disabled
          this.isLoading = false;
        } else {
          // TODO: some error message to the UI about invalid target currency
          console.log("Invalid target currency given");
        }
      });
  }

  private addCurveToChart(fxRates: FxRate[]) {
    // Define local Label and ChartDataSets
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

    // Set curves Label for chartDataSet
    chartDataSet.label = fxRates[0].sourceCurrency.alphabeticCode + '/' + fxRates[0].targetCurrency.alphabeticCode;

    // Push chartDataSet into this.lineChartData to draw curve on the graph
    this.lineChartData.push(chartDataSet);

    // Overwrite labels as it should be the same for every additional curve
    this.lineChartLabels = labels;
  }
}
