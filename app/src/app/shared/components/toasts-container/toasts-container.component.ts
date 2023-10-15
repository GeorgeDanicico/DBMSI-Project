import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Observable } from 'rxjs';
import { NotificationToast, NotificationType } from './models.ts/toasts.model';
import { ToastService } from './toasts-service';

@Component({
  selector: 'ado-toasts-container',
  templateUrl: './toasts-container.component.html',
  styleUrls: ['./toasts-container.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ToastsContainerComponent {
  toast$: Observable<NotificationToast | null>;

  constructor(public toastService: ToastService) {
    this.toast$ = toastService.toastSubject;
  }

  computeNotificationIcon(type: NotificationType): string | undefined {
    switch (type) {
      case NotificationType.ERROR:
        return 'fa-xmark';
      case NotificationType.INFO:
        return 'fa-circle-info';
      case NotificationType.WARNING:
        return 'fa-triangle-exclamation';
      case NotificationType.SUCCESS:
        return 'fa-check';
    }
  }

  onClose(): void {
    this.toastService.remove();
  }
}
