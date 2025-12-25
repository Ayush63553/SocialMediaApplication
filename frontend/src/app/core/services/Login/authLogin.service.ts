import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Router } from '@angular/router';

import { BehaviorSubject, Observable } from 'rxjs';

import { tap } from 'rxjs/operators';

export interface LoginPayload {

  username: string;

  password: string;

}

@Injectable({

  providedIn: 'root'

})

export class AuthService {

  private USER_ID='userId';

  private apiUrl = 'http://localhost:8765/api/auth';

  private tokenKey = 'mysecretkeymysecretkeymysecretkey12345';

  private logoutTimer: any;

  private loggedIn=new BehaviorSubject<boolean|null>(this.hasValidToken());
  
  isLoggedIn$=this.loggedIn.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(payload: LoginPayload): Observable<any> {

    return this.http.post<{ token: string,userId:number }>(`${this.apiUrl}/login`, payload).pipe(

      tap(response => {
        if(response.userId){
        localStorage.setItem(this.USER_ID,response.userId.toString());
      }

        if (response.token) {
 
         
          localStorage.setItem(this.tokenKey, response.token);
         

          this.startAutoLogoutTimer(response.token);
          this.loggedIn.next(true);
        }
   

      })

    );

  }

  loginp(userId:number){
    localStorage.setItem(this.USER_ID,userId.toString());
  }

  logout(): void {

    localStorage.removeItem(this.tokenKey);
    if (this.logoutTimer) clearTimeout(this.logoutTimer);
    this.loggedIn.next(null);
    this.router.navigate(['/auth']);
  }

  getToken(): string | null {

    return localStorage.getItem(this.tokenKey);

  }

  isLoggedIn(): boolean {

    const token = this.getToken();

    if (!token) return false;

    const payload = this.decodeToken(token);

    if (!payload) return false;

    const expiry = payload.exp * 1000; // seconds → ms

    return Date.now() < expiry;

  }

  private decodeToken(token: string): any {

    try {
      return JSON.parse(atob(token.split('.')[1]));

    } catch (e) {

      return null;

    }

  }

  private hasValidToken():boolean{
    const token=this.getToken();
    if(!token) return false;
    const payload=this.decodeToken(token);
    if(!payload) return false;
    const expiry=payload.exp*1000;
    return Date.now()<expiry;
  }

  getUserId():number{
    const id=localStorage.getItem(this.USER_ID)
    return Number(id);
  }

  // Auto logout based on token expiry

  private startAutoLogoutTimer(token: string): void {

    const payload = this.decodeToken(token);

    if (!payload) return;

    const expiry = payload.exp * 1000;

    const timeLeft = expiry - Date.now();

    if (this.logoutTimer) clearTimeout(this.logoutTimer);

    this.logoutTimer = setTimeout(() => {

      alert('Session expired. Please log in again.');

      this.logout();

    }, timeLeft);

  }

}