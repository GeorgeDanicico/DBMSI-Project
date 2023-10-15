import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { NotificationToast, NotificationType } from './models.ts/toasts.model';

@Injectable({ providedIn: 'root' })
export class ToastService implements OnDestroy {
  private _toastSubject = new BehaviorSubject<NotificationToast | null>(null);

  public get toastSubject(): Observable<NotificationToast | null> {
    return this._toastSubject.asObservable();
  }

  showStandard(message: string): void {
    this.show(message);
  }

  showSuccess(message: string): void {
    this.show(message, NotificationType.SUCCESS, {
      className: 'bg-success text-light',
      delay: 5000,
    });
  }

  showError(message: string): void {
    this.show(message, NotificationType.ERROR, {
      className: 'bg-danger text-light',
      delay: 5000,
    });
  }

  showWarning(message: string): void {
    this.show(message, NotificationType.WARNING, {
      className: 'bg-warning text-black',
      delay: 5000,
    });
  }

  showInfo(message: string): void {
    this.show(message, NotificationType.INFO, {
      className: 'bg-info text-black',
      delay: 5000,
    });
  }

  remove(): void {
    this._toastSubject.next(null);
  }

  ngOnDestroy(): void {
    this.remove();
  }

  private show(
    message: string,
    type: NotificationType = NotificationType.STANDARD,
    options = {}
  ): void {
    this._toastSubject.next({ message, type, ...options });
  }
}
