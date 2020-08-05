import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { takeUntil } from "rxjs/operators";
import { MatDialog } from "@angular/material/dialog";
import { FxrateChartComponent } from "../fxrate-chart.component";

@Component({
  selector: 'app-modal-container',
  template: '',
  // styles: [] // Container will open up the chart component, so ir doesn't really need a markup of its own
})
export class ModalContainerComponent implements OnInit, OnDestroy {

  destroy = new Subject<any>();
  currentDialog = null;

  constructor(private dialog: MatDialog, private activatedRoute: ActivatedRoute, private router: Router) {
  }

  // When router navigates on this component ngOnInit method is fired up
  ngOnInit(): void {

    // Take params from activatedRoute
    this.activatedRoute.params.pipe(takeUntil(this.destroy)).subscribe(params => {

      // Fire up FxrateChartComponent as a modal
      this.currentDialog = this.dialog.open(FxrateChartComponent, {
        width: '100%'
      });

      // Pass currency parameter to chart component opened in modal dialog
      this.currentDialog.componentInstance.modalCurrencyParam = params.currency;

      // Then modal is closed update url and let user scroll from previous position as nothing had happened
      this.currentDialog.afterClosed().subscribe(result => {
        this.router.navigate([ '../' ], { relativeTo: this.activatedRoute });
      });

    });
  }

  ngOnDestroy() {
    this.destroy.next();
  }
}
