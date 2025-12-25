import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/core/services/Login/authLogin.service';
import { NotificationService } from 'src/app/core/services/notification.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  isLoggedIn: boolean | null = false;
  showLogoutToast: boolean = false;

  notifications: string[] = [];
  unreadCount: number = 0;

  constructor(
    private authService: AuthService,
    private cdRef: ChangeDetectorRef,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe((status) => {
      this.isLoggedIn = status;

      if (status) {
        const userId = Number(localStorage.getItem('userId'));
        this.notificationService.getNotifications(userId).subscribe({
          next: (data) => {
            this.notifications = data;
            this.notificationService.updateCount(data.length); // 🔥 push to service
          },
          error: (err) => console.error('Failed to load notifications', err),
        });
      }
    });

    // subscribe to live count updates
    this.notificationService.notificationsCount$.subscribe((count) => {
      this.unreadCount = count;
      this.cdRef.detectChanges();
    });
  }

  LogOut(): void {
    this.authService.logout();
    this.showLogoutToast = true;

    setTimeout(() => {
      this.showLogoutToast = false;
    }, 3000);
  }
}