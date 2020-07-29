import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';

import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { ChartsModule } from "ng2-charts";

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { FxratesComponent } from './fxrates/fxrates.component';
import { FxrateListComponent } from './fxrates/fxrate-list/fxrate-list.component';
import { CalcExchangeComponent } from './fxrates/calc-exchange/calc-exchange.component';
import { FxrateChartComponent } from './fxrates/fxrate-chart/fxrate-chart.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FxratesComponent,
    FxrateListComponent,
    CalcExchangeComponent,
    FxrateChartComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    MatToolbarModule,
    MatIconModule,
    MatPaginatorModule,
    MatTableModule,
    MatSortModule,
    MatProgressBarModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    ChartsModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
