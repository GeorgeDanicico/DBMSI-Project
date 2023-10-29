import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ViewRowsComponent } from './components/view-rows.component';


const routes: Routes = [
  {
    path: '',
    component: ViewRowsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ViewRowsRoutingModule {}
