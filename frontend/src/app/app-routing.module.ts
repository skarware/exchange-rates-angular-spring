import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FxrateListComponent } from './fxrates/fxrate-list/fxrate-list.component';
import { FxratesComponent } from './fxrates/fxrates.component';
import { CalcExchangeComponent } from './fxrates/calc-exchange/calc-exchange.component';
import { FxrateChartComponent } from "./fxrates/fxrate-chart/fxrate-chart.component";
import { ModalContainerComponent } from "./fxrates/fxrate-chart/shared/modal-container.component";

// Routes config tells the Router which view to display when a user clicks a link or pastes a URL into the browser address bar.
const routes: Routes = [
  { path: '', component: FxratesComponent },
  // Set up FxRates list parent path and children path for Routable Modal technique, to enable to open up a modal that is tied to specific route.
  {
    path: 'fxrate-list', component: FxrateListComponent,
    children: [
      {
        path: ':currency', component: ModalContainerComponent,
      }
    ]
  },
  { path: 'fxrate-chart', component: FxrateChartComponent },
  { path: 'calc-exchange', component: CalcExchangeComponent },
];


// The @NgModule metadata initializes the router and starts it listening for browser location changes.
@NgModule({
  /*
   The following line adds the RouterModule to the AppRoutingModule imports array
   and configures the router at the application's root level with the routes in one step
  */
  imports: [ RouterModule.forRoot(routes) ],
  // AppRoutingModule exports RouterModule so it will be available throughout the app.
  exports: [ RouterModule ]
})
export class AppRoutingModule {
}
