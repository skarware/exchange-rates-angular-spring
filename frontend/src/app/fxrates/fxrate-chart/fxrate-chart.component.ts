import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { BaseChartDirective, Color, Label } from "ng2-charts";
import { ChartDataSets, ChartOptions } from "chart.js";
import { FxRateService } from "../shared/fxrate.service";
import { ActivatedRoute, ParamMap, Router } from "@angular/router";
import { FxRate } from "../shared/fxrate.model";
import { CurrencyModel } from "../shared/currency.model";
import { CalcExchangeService } from "../shared/calc-exchange.service";
import { NgForm } from "@angular/forms";

@Component({
  selector: 'app-fxrate-chart',
  templateUrl: './fxrate-chart.component.html',
  styleUrls: [ './fxrate-chart.component.css' ]
})
export class FxrateChartComponent implements OnInit {
  // Define Material spinner display/hide boolean
  public isLoading: boolean;

  public lineChartType = 'line';
  public lineChartOptions: ChartOptions = {
    responsive: true,
    scales: {
      xAxes: [ {
        type: 'time',
        time: {
          unit: 'day'
        }
      } ],
      // yAxes: [ {
      //   scaleLabel: {
      //     display: true,
      //     labelString: 'Exchange Rate'
      //   }
      // } ]
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
  public lineChartColors: Color[] = [
    {
      backgroundColor: 'rgb(0,0,255, 0.3)',
      borderColor: 'blue',
    },
    {
      backgroundColor: 'rgb(0,255,0, 0.3)',
      borderColor: 'green',
    },
    {
      backgroundColor: 'rgba(255,0,0,0.3)',
      borderColor: 'red',
    }
  ];

  // Define Chart data and labels with empty arrays or embrace for errors
  public lineChartLabels: Label[] = [];
  public lineChartData: ChartDataSets[] = [];

  //
  @ViewChild(BaseChartDirective, { static: false }) chart: BaseChartDirective;

  // Initially chart view mode is not modal
  isViewModeModal = false;

  // Input bound property comes from ModalContainerComponent
  @Input() modalCurrencyParam: string;

  // "EUR", "USD", "GBP", ...
  currencyOptions: CurrencyModel[];

  // Define array for selected currencies
  selectedCurrencies: CurrencyModel[] = [];

  // Inject FxRateService into this component as private class member
  constructor(
    private calcExchangeService: CalcExchangeService,
    private fxRateService: FxRateService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
  ) {
  }

  ngOnInit(): void {
    this.isLoading = true;

    // If component is in modal view mode show chart for given currency in param
    if (this.modalCurrencyParam) {

      // If currency param exists set up component into modal view mode
      this.isViewModeModal = true;

      // Add given currency curve into chart
      this.fetchAndAddCurveToChart(this.modalCurrencyParam);

    } else {

      // Fetch available currencies from API
      this.calcExchangeService.fetchCurrencies()
        .subscribe((currencyOptions: CurrencyModel[]) => {
          // Update this.currencyOptions with new data
          this.currencyOptions = currencyOptions;

          // Look for query parameters in URL path
          this.activatedRoute.queryParamMap
            .subscribe((paramMap: ParamMap) => {

              // If query params exist, Get params for fetching FxRate data and drawing curve into the chart
              if (paramMap.has('0')) {
                this.selectedCurrencies[0] = this.currencyOptions.find(ccy => ccy.alphabeticCode === paramMap.get('0').toUpperCase());
                if (paramMap.has('1')) {
                  this.selectedCurrencies[1] = this.currencyOptions.find(ccy => ccy.alphabeticCode === paramMap.get('1').toUpperCase());
                  if (paramMap.has('2')) {
                    this.selectedCurrencies[2] = this.currencyOptions.find(ccy => ccy.alphabeticCode === paramMap.get('2').toUpperCase());
                  }
                }
              } else {
                // Else, Select default currencies for the chart
                this.selectedCurrencies[0] = this.currencyOptions.find(ccy => ccy.alphabeticCode === 'USD');
                this.selectedCurrencies[1] = this.currencyOptions.find(ccy => ccy.alphabeticCode === 'GBP');
                this.selectedCurrencies[2] = this.currencyOptions.find(ccy => ccy.alphabeticCode === 'CHF');
              }

              // Reset Chart data before redrawing curves to avoid duplication
              this.lineChartData = [];

              // Add given currency curves into the chart
              this.fetchAndAddCurveToChart(this.selectedCurrencies[0]?.alphabeticCode);
              this.fetchAndAddCurveToChart(this.selectedCurrencies[1]?.alphabeticCode);
              this.fetchAndAddCurveToChart(this.selectedCurrencies[2]?.alphabeticCode);

            });
        });
    }
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

          // Update chart curve colors after new data pushed into ChartDataSets,
          // '?' because this.chart will be undefined until first data is pushed into ChartDataSets
          this.chart?.updateColors();

        } else {
          console.info("Invalid target currency given");
        }
      });
  }

  private addCurveToChart(fxRates: FxRate[]) {
    // Define local Label and ChartDataSets
    const labels: Label[] = [];
    const chartDataSet: ChartDataSets = { data: [], label: '' };

    fxRates.forEach((fxRate, index) => {
      // Extract currencies exchange rate and effective date
      const date = fxRate.effectiveDate;
      const rate = fxRate.exchangeRate;

      // Push extracted data from FxRate into new ChartDataSet obj
      chartDataSet.data.push(rate);
      labels.push(date);
    })

    // Set curves Label for chartDataSet
    chartDataSet.label = fxRates[0].sourceCurrency.alphabeticCode + '/' + fxRates[0].targetCurrency.alphabeticCode + ' (' + fxRates[0].targetCurrency.displayName + ')';

    // Push chartDataSet into this.lineChartData to draw curve on the graph
    this.lineChartData.push(chartDataSet);

    // Overwrite labels as it should be the same for every additional curve
    this.lineChartLabels = labels;
  }

  // Method fired on selection change in input fields to change url
  onSelectionChangeURL() {

    // Change url link to reflect selected currencies
    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: [
        this.selectedCurrencies[0]?.alphabeticCode,
        this.selectedCurrencies[1]?.alphabeticCode,
        this.selectedCurrencies[2]?.alphabeticCode,
      ],
    });

  }

}
