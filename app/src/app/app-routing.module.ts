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
