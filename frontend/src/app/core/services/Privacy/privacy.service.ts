import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface privacyDTO{
  privacyId:number;
  postId:number;
  userId:number;
}

@Injectable({
  providedIn: 'root'
})

export class PrivacyService {
  constructor(private http:HttpClient) { }

  private apiUrl = 'http://localhost:8765/api/post-privacy';

  addRule(postId:any,userId:any):Observable<string>{
    return this.http.post<string>(`${this.apiUrl}/${postId}/add-rule?userId=${userId}`,{},{responseType:'text' as 'json'});
  }

  getRules(postId:any):Observable<privacyDTO[]>{
    return this.http.get<privacyDTO[]>(`${this.apiUrl}/${postId}/rules`);
  }

  deleteRules(postId:any):Observable<string>{
    return this.http.delete<string>(`${this.apiUrl}/${postId}/delete`,{responseType:'text' as 'json'});
  }
}
