import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LayoutComponent } from './ui/layout/layout.component';

const routes: Routes = [
  { path: '', redirectTo: 'databases', pathMatch: 'full' },

  {
    path: '',
    component: LayoutComponent,
    children: [{
      path: 'databases',
      loadChildren: () => import('./databases/databases.module').then(m => m.DatabasesModule)
    },
    {
      path: 'databases/:databaseName/tables/:tableName',
      loadChildren: () => import('./view-rows/view-rows.module').then(m => m.ViewRowsModule)
    },
    {
      path: 'databases/:databaseName/tables/:tableName/select',
      loadChildren: () => import('./select-screen/select-screen.module').then(m => m.SelectScreenModule)
    }],
  },

  {
    path: '**',
    redirectTo: 'databases',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
