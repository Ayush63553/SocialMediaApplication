import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

export interface RegisterPayload {

  firstName: string;

  lastName: string;

  email: string;

  username:string,

  password: string;

  dateOfBirth: string;

  gender: string;

  bio:string;

  profilePictureUrl:string;

}

@Injectable({

  providedIn: 'root'

})

export class AuthService {

  private apiUrl = 'http://localhost:8765/api/users';

  constructor(private http: HttpClient) {}

  registerUser(payload: RegisterPayload): Observable<any> {

    return this.http.post(`${this.apiUrl}`, payload);

  }
  setSecurityQuestion(payload:{userId:number,question:string,answer:string}){
    return this.http.post('http://localhost:8765/api/auth/set-security-question',payload,{responseType:'text'})
  }
  getSecurityQuestion(userId:number){
    return this.http.get(`http://localhost:8765/api/auth/get-question/${userId}`,{responseType:'text'})
  }

}
