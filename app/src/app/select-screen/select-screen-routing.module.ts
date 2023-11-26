import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SelectScreenComponent } from './select-screen.component';


const routes: Routes = [
  {
    path: '',
    component: SelectScreenComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SelectScreenRoutingModule {}
