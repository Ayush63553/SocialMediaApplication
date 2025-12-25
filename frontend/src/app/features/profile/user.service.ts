import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  userId: number;
  username: string;
  firstName?: string;
  lastName?: string;
  dateOfBirth?: string;
  bio?: string;
  profilePictureUrl?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:8765/api/users';

  constructor(private http: HttpClient) {}

  // Get user by ID
  getUser(id: any): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${id}`);
  }

  // Update user with FormData (backend expects multipart/form-data)
  updateUser(id: number, formData: FormData): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, formData);
  }

  // Delete profile photo
  removeProfilePhoto(userId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/profilePhotoDelete/${userId}`);
  }
  
  getUserByUsername(username:string):Observable<User>{
    return this.http.get<User>(`${this.baseUrl}/username/${username}`);
  }
  getFriends(userId: number): Observable<User[]> {
    return this.http.get<User[]>(`http://localhost:8765/api/friend-requests/friends/${userId}`);
  }
  getFriendSuggestions(userId: number): Observable<User[]> {
    return this.http.get<User[]>(`http://localhost:8765/api/friend-requests/friends/find/${userId}`);
  }
  sendFriendRequest(senderId: number, receiverId: number): Observable<any> {
    return this.http.post(`http://localhost:8765/api/friend-requests/send?senderId=${senderId}&receiverId=${receiverId}`, {}, { responseType: 'text' });
  }
  findMutualFriend(userId:any,receiverId:any): Observable<User[]>{
      return this.http.get<User[]>(`http://localhost:8765/api/friend-requests/friends/find?userId=${userId}&friendId=${receiverId}`);
    }

}