import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

const BASE = 'http://localhost:8765';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {

  private notificationsCountSubject = new BehaviorSubject<number>(0);
  notificationsCount$ = this.notificationsCountSubject.asObservable();
  notifications: string[] = [];
  loading: boolean = true;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      this.loading = false;
      return;
    }

    this.http.get<string[]>(`${BASE}/api/friend-requests/notifications/${userId}`)
      .subscribe({
        next: (data) => {
          this.notifications = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Failed to load notifications', err);
          this.loading = false;
        }
      });
  }

  updateCount(count : number): void {
    this.notificationsCountSubject.next(count);
  }
}