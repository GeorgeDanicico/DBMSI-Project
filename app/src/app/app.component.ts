import { Component } from '@angular/core';

@Component({
  selector: 'ado-root',
  template: `
    <router-outlet></router-outlet>
    <ado-toasts-container aria-live="polite" aria-atomic="true"></ado-toasts-container>
  `,
})
export class AppComponent {}
