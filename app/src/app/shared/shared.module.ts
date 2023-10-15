import { NgModule } from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastsContainerComponent } from './components/toasts-container/toasts-container.component';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [ToastsContainerComponent],
  imports: [CommonModule, NgbModule, HttpClientModule, FormsModule, ReactiveFormsModule],
  exports: [CommonModule, NgbModule, HttpClientModule, FormsModule, ReactiveFormsModule, ToastsContainerComponent],
  providers: [],
})
export class SharedModule {}
