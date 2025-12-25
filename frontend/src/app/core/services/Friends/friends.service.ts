import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

export interface Friend {

  userId: number;

  username: string;

  profilePictureUrl?: string;
  bio?:string;

}

export interface Request{
  requestId:number;
  sender:UserDTO;
  receiver:UserDTO;
}

export interface UserDTO{
  userId:number,
  username:string,
  email?:string,
  bio?:string,
  firstName:string,
  lastName:string,
  dateOfBirth?:Date,
  gender?:string,
  profilePictureUrl?:string,
  relationshipStatus?:string,
  selected?:boolean
} 

@Injectable({

  providedIn: 'root'

})

export class FriendsService {

  private apiUrl = 'http://localhost:8765/api/friend-requests';

  constructor(private http: HttpClient) {}

  getFriends(userId:any): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}/friends/${userId}`);
  }

  getFriendRequestsReceived(userId:any): Observable<Request[]> {
    return this.http.get<Request[]>(`${this.apiUrl}/received/${userId}`);
  }

  getFriendRequestsSent(userId:any): Observable<Request[]> {
    return this.http.get<Request[]>(`${this.apiUrl}/sent/${userId}`);
  }

  sendFriendRequest(userId:any, receiverId: number): Observable<string> {

    return this.http.post<string>(`${this.apiUrl}/send?senderId=${userId}&receiverId=${receiverId}`,{responseType:'text' as 'json'});

  }

  acceptFriendRequest(userId: number,receiverId:any): Observable<string> {

    return this.http.put<string>(`${this.apiUrl}/accept?senderId=${userId}&receiverId=${receiverId}`, {responseType:'text' as 'json'});

  }

  rejectFriendRequest(userId: number,receiverId:any): Observable<string> {

    return this.http.put<string>(`${this.apiUrl}/reject?senderId=${userId}&receiverId=${receiverId}`, {responseType:'text' as 'json'});

  }
  
  cancelFriendRequest(userId: any,receiverId:number): Observable<string> {

    return this.http.delete<string>(`${this.apiUrl}/cancel?senderId=${userId}&receiverId=${receiverId}`,{responseType:'text' as 'json'});

  }

  findFriends(userId: any): Observable<UserDTO[]> {

    return this.http.get<UserDTO[]>(`${this.apiUrl}/friends/find/${userId}`);

  }

  unFriend(userId:any,receiverId:number):Observable<string>{
      return this.http.delete<string>(`${this.apiUrl}/friends/delete?userId=${userId}&friendId=${receiverId}`,{responseType:'text' as 'json'});
  }

  findMutualFriend(userId:any,receiverId:any): Observable<UserDTO[]>{
    return this.http.get<UserDTO[]>(`${this.apiUrl}/friends/find?userId=${userId}&friendId=${receiverId}`);
  }

  getProfile(userId:any, friendId:any): Observable<UserDTO>{
    return this.http.get<UserDTO>(`${this.apiUrl}/profile?userId=${userId}&friendId=${friendId}`);
  }

  getStatus(userId:any,receiverId:number) : Observable<string>{
    return this.http.get<string>(`${this.apiUrl}/relationship-status?userId=${userId}&friendId=${receiverId}`,{responseType:'text' as 'json'});
  }

  blockUser(userId:any, blockedUserId:any) : Observable<string>{
    return this.http.post<string>(`${this.apiUrl}/blockUser?userId=${userId}&blockedUserId=${blockedUserId}`,{}, {responseType:'text' as 'json'});
  }

  getBlockedUser(userId:any, blockedUserId:any): Observable<UserDTO>{
    return this.http.get<UserDTO>(`${this.apiUrl}/blockedUser?userId=${userId}&blockedUserId=${blockedUserId}`);
  }

  getBlockedUsers(userId:any): Observable<UserDTO[]>{
    return this.http.get<UserDTO[]>(`${this.apiUrl}/blockedUsers/${userId}`);
  }

  unBlockUser(userId:any, blockedUserId:any): Observable<string>{
    return this.http.delete<string>(`${this.apiUrl}/unBlockUser?userId=${userId}&blockedUserId=${blockedUserId}`,{responseType:'text' as 'json'});
  }

  checkIfBlockedUserExists(userId:any, blockedUserId:any): Observable<boolean>{
    return this.http.get<boolean>(`${this.apiUrl}/isBlocked?userId=${userId}&blockedUserId=${blockedUserId}`);
  }
}