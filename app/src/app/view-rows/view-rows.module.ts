import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ViewRowsComponent } from './components/view-rows.component';
import { ViewRowsRoutingModule } from './view-rows-routing.module';
import { SharedModule } from '../shared/shared.module';
import { InsertValueModalComponent } from './components/modals/insert-value-modal/insert-value-modal.component';

@NgModule({
  declarations: [ViewRowsComponent, InsertValueModalComponent],
  imports: [ViewRowsRoutingModule, SharedModule],
})
export class ViewRowsModule {}
