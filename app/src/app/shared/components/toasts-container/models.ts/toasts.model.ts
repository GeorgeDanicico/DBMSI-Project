export enum NotificationType {
  ERROR = 'ERROR',
  WARNING = 'WARNING',
  INFO = 'INFO',
  SUCCESS = 'SUCCESS',
  STANDARD = 'STANDARD',
}

export class NotificationToast {
  message!: string;
  type!: NotificationType;
  delay?: number;
  className?: string;
}
