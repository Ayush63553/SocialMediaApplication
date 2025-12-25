// src/app/main/services/profile.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Like } from 'src/app/models/like.model';
import { Comment } from 'src/app/models/comment.model';
import { Post } from 'src/app/models/post.model';


@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private baseUrl = 'http://localhost:8765'; // backend

  constructor(private http: HttpClient) {}

  // User
  getUser(userId: any): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/users/${userId}`);
  }

  // Posts
  getUserPosts(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/posts/user/${userId}`);
  }

  getUserProfilePosts(userId:any,profileId:any):Observable<any[]>{
    return this.http.get<any[]>(`${this.baseUrl}/api/posts/profile?userId=${userId}&friendId=${profileId}`);
  }

  getPostById(postId:number):Observable<Post>{
    return this.http.get<Post>(`${this.baseUrl}/api/posts/${postId}`)
  }

  createPost(userId: number, formData: FormData): Observable<Post> {
    return this.http.post<Post>(`${this.baseUrl}/api/posts/userpost/${userId}`, formData);
  }

  updatePost(postId: number, formData: FormData): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/api/posts/${postId}`, formData);
  }

  deletePost(postId: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/api/posts/${postId}`,{responseType:'text' as 'json'});
  }

  // Likes
  getLikesByPost(postId: number): Observable<Like[]> {
    return this.http.get<Like[]>(`${this.baseUrl}/api/likes/${postId}`);
  }

  likePost(dto: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/api/likes`, dto);
  }

  unlikePost(postId: number, userId: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/api/likes/${postId}/user/${userId}`);
  }

  // Comments
  getComments(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.baseUrl}/api/comments/post/${postId}`);
  }

  deleteComment(commentId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/comments/${commentId}?userId=${userId}`);
  }

  addComment(commentDto: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/api/comments`, commentDto);
  }
}