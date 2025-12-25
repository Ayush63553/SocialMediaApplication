import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { User, UserService } from '../../profile/user.service';
import { FriendsService, UserDTO } from 'src/app/core/services/Friends/friends.service';
import { Router } from '@angular/router';

const BASE = 'http://localhost:8765';

interface Post {
  postId: number;
  userId: number;
  username: string;
  content: string;
  privacy?: string;
  mediaUrl?: string;
  mediaType?: string;
  profilePictureUrl?: string;
  likeCount: number;
  likedByUsers: string[];
  createdAt?: string;
  comments: Comment[];
}

interface Comment {
  commentId: number;
  postId: number;
  userId: number;
  username: string;
  content: string;
  createdAt?: string;
  profilePictureUrl?: string;
}

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit {
  posts: Post[] = [];
  friends: User[] = [];
  suggestions: User[] = [];
  commentsMap: { [postId: number]: Comment[] } = {};
  showCommentsMap: { [postId: number]: boolean } = {};
  newCommentText: { [postId: number]: string } = {};

  loggedInUserId!: number;
  loggedInUsername!: string;
  isNewUser: boolean = false;

  // Toast state
  toastMessage: string = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast: boolean = false;

  constructor(private http: HttpClient, private userService: UserService, 
    private cdRef: ChangeDetectorRef, private router:Router, private friendsService:FriendsService) {}

  ngOnInit(): void {
    this.userService.getUser(localStorage.getItem('userId')).subscribe({
      next: (user: User) => {
        this.loggedInUserId = user.userId;
        this.loggedInUsername = user.username;
        this.loadFeed();
        this.loadFriends();
        this.loadSuggestions();
      },
      error: (err) => console.error('Failed to load logged-in user', err)
    });
  }

  private showToaster(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    this.cdRef.detectChanges();
    setTimeout(() => {
      this.showToast = false;
      this.cdRef.detectChanges();
    }, 3000);
  }

  // Load posts (friends + own posts)
  loadFeed() {
    this.http.get<Post[]>(`${BASE}/api/posts/feed/${this.loggedInUserId}`).subscribe(data => {
      this.posts = data.map(p => ({
        ...p,
        likedByUsers: p.likedByUsers || [],
        comments: p.comments || []
      }));
      
      this.posts.forEach(p => {
        this.showCommentsMap[p.postId] = false;
        this.commentsMap[p.postId] = p.comments || [];
      });

      // Check if new user (no friends and no posts)
      this.isNewUser = (this.posts.length === 0 && this.friends.length === 0);
    }, err => console.error('Failed to load posts', err));
  }

  // Load friends
  loadFriends() {
    this.userService.getFriends(this.loggedInUserId).subscribe({
      next: (friends) => {
        this.friends = friends;
        this.isNewUser = (this.posts.length === 0 && this.friends.length === 0);
         for (let i of this.friends) {
          this.userService.findMutualFriend(localStorage.getItem("userId"), i.userId).subscribe({
            next: (data) => {
              this.mutualFriendsMap[i.userId] = data;
            },
            error: (err) => console.error('Error loading mutual friends', err)
          });    
        }
      },
      error: (err) => console.error('Failed to load friends', err)
    });
  }

  viewProfile(friend: User): void {
      this.showToaster(`👤 Viewing profile of ${friend.username}`, 'info');
      this.router.navigate(['friends/profile', friend.userId]);
  }

  // Load friend suggestions
  loadSuggestions() {
    this.userService.getFriendSuggestions(this.loggedInUserId).subscribe({
      next: (users) => { 
        this.suggestions = users; 
        for (let i of this.suggestions) {
          this.userService.findMutualFriend(localStorage.getItem("userId"), i.userId).subscribe({
            next: (data) => {
              this.mutualFriendsMap[i.userId] = data;
            },
            error: (err) => console.error('Error loading mutual friends', err)
          });    
        }
      },
      error: (err) => console.error('Failed to load suggestions', err)
    });
  }

  // Send friend request
  sendFriendRequest(receiverId: number) {
    this.userService.sendFriendRequest(this.loggedInUserId, receiverId).subscribe({
      next: () => {
        this.suggestions = this.suggestions.filter(u => u.userId !== receiverId);
        this.showToaster('✅ Friend request sent!', 'success');
      },
      error: (err) => {
        console.error('Failed to send friend request', err);
        this.showToaster('❌ Failed to send friend request', 'error');
      }
    });
  }

  // Toggle like/unlike
  toggleLike(post: Post) {
    const payload = { postId: post.postId, userId: this.loggedInUserId };

    if (post.likedByUsers.includes(this.loggedInUsername)) {
      this.http.delete(`${BASE}/api/likes/${post.postId}/user/${this.loggedInUserId}`)
        .subscribe(() => {
          post.likeCount = Math.max(0, post.likeCount - 1);
          post.likedByUsers = post.likedByUsers.filter(u => u !== this.loggedInUsername);
        }, err => console.error('Failed to unlike', err));
    } else {
      this.http.post<any>(`${BASE}/api/likes`, payload).subscribe({
        next: (res) => {
          post.likeCount = res?.likeCount ?? (post.likeCount + 1);
          if (!post.likedByUsers.includes(this.loggedInUsername)) {
            post.likedByUsers.push(this.loggedInUsername);
          }
        },
        error: (err) => console.error('Failed to like', err)
      });
    }
  }

  // Dislike = unlike
  dislikePost(post: Post) {
    this.http.delete(`${BASE}/api/likes/${post.postId}/user/${this.loggedInUserId}`, { responseType: 'text' })
      .subscribe(() => {
        if (post.likedByUsers.includes(this.loggedInUsername)) {
          post.likeCount = Math.max(0, post.likeCount - 1);
          post.likedByUsers = post.likedByUsers.filter(u => u !== this.loggedInUsername);
        }
      }, err => console.error('Dislike failed', err));
  }

  // Toggle comments
  toggleComments(post: Post) {
    this.showCommentsMap[post.postId] = !this.showCommentsMap[post.postId];
    if (this.showCommentsMap[post.postId]) {
      this.loadComments(post.postId);
    }
  }

  // Load comments
  loadComments(postId: number) {
    this.http.get<Comment[]>(`${BASE}/api/comments/post/${postId}`)
      .subscribe(data => {
        this.commentsMap[postId] = data.map(c => ({
          ...c,
          username: c.username || `User ${c.userId}`
        }));
      }, err => console.error('Failed to load comments', err));
  }

  // Add comment
  addComment(post: Post) {
    const text = (this.newCommentText[post.postId] || '').trim();
    if (!text) return;

    const payload = { postId: post.postId, userId: this.loggedInUserId, content: text };
    this.http.post<Comment>(`${BASE}/api/comments`, payload)
      .subscribe(created => {
        created.username = this.loggedInUsername;
        this.commentsMap[post.postId] = this.commentsMap[post.postId] || [];
        this.commentsMap[post.postId].push(created);
        this.newCommentText[post.postId] = '';
      }, err => console.error('Failed to add comment', err));
  }

  // Delete comment
  deleteComment(post: Post, comment: Comment) {
    this.http.delete(`${BASE}/api/comments/${comment.commentId}?userId=${this.loggedInUserId}`)
      .subscribe(() => {
        this.commentsMap[post.postId] =
          this.commentsMap[post.postId].filter(c => c.commentId !== comment.commentId);
        this.showToaster('🗑️ Comment deleted', 'info');
      }, err => console.error('Failed to delete comment', err));
  }

  canDeleteComment(post: Post, comment: Comment) {
    return comment.userId === this.loggedInUserId || post.userId === this.loggedInUserId;
  }

  

  expanded: { [userId: number]: boolean } = {};
  mutualFriendsMap: { [userId: number]: User[] } = {};
  ObjectKeys = Object.keys;
  toggleDropdown(friendId: number): void {
    this.expanded[friendId] = !this.expanded[friendId];
  }
}