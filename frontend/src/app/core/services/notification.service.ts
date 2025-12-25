import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

const BASE = 'http://localhost:8765';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsCountSubject = new BehaviorSubject<number>(0);
  notificationsCount$ = this.notificationsCountSubject.asObservable();

  constructor(private http: HttpClient) {}

  getNotifications(userId: number): Observable<string[]> {
    return this.http.get<string[]>(`${BASE}/api/friend-requests/notifications/${userId}`);
  }

  updateCount(count: number): void {
    this.notificationsCountSubject.next(count);
  }
}