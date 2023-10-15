import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DatabasesComponent } from './components/databases.component';

const routes: Routes = [
  {
    path: '',
    component: DatabasesComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DatabasesRoutingModule {}
