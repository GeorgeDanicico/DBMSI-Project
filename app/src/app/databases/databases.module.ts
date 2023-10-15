import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CreateIndexModalComponent } from './components/create-index-modal/create-index-modal.component';
import { CreateTableModalComponent } from './components/create-table-modal/create-table-modal.component';
import { DatabasesListComponent } from './components/databases-list/databases-list.component';
import { DatabasesComponent } from './components/databases.component';
import { TableDetailedComponent } from './components/table-detailed/table-detailed.component';
import { TablesListComponent } from './components/tables-list/tables-list.component';
import { TablesComponent } from './components/tables/tables.component';
import { DatabasesRoutingModule } from './databases-routing.module';

@NgModule({
  declarations: [
    DatabasesComponent,
    DatabasesListComponent,
    TablesComponent,
    TablesListComponent,
    CreateTableModalComponent,
    TableDetailedComponent,
    CreateIndexModalComponent,
  ],
  imports: [DatabasesRoutingModule, ReactiveFormsModule, CommonModule, HttpClientModule, FormsModule],
})
export class DatabasesModule {}
