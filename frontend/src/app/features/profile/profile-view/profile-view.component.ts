import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FriendsService } from 'src/app/core/services/Friends/friends.service';
import { AuthService } from 'src/app/core/services/Login/authLogin.service';
import { ProfileService } from 'src/app/main/services/profile.service';

@Component({
  selector: 'app-profile-view',
  templateUrl: './profile-view.component.html',
  styleUrls: ['./profile-view.component.css']
})
export class ProfileViewComponent implements OnInit {
  user: any = {};
  posts: any[] = [];
  currentUserId: number;
  postCount:number=0;

  // Toast state
  toastMessage: string = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast: boolean = false;

  // Delete confirmation state
  pendingDeleteId: number | null = null;

  constructor(
    private profileService: ProfileService,
    private router: Router,
    private authService:AuthService,
    private friendsService:FriendsService
  ) {
    this.currentUserId = this.authService.getUserId();
  }

  ngOnInit(): void {
    this.loadUser();
    this.loadPosts();
  }

  // Toast helper
  showToaster(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }

  loadUser(): void {
    this.profileService.getUser(this.currentUserId).subscribe({
      next: (u) => {
        this.user = {
          name: `${u.firstName || ''} ${u.lastName || ''}`.trim() || u.username,
          avatar: u.profilePictureUrl || 'https://tse3.mm.bing.net/th/id/OIP.F977i9e7dMrznvOT8q8azgHaEf?rs=1&pid=ImgDetMain&o=7&rm=3',
          location: u.location || '',       
          profession: u.profession || '',
          education: u.education || '',
          bio: u.bio || '',
          friends: u.friends || 0,
          photos: u.photos || 0,
          comments: u.comments || 0
        };
        this.friendsService.getFriends(localStorage.getItem('userId')).subscribe({
          next:data=>{
            this.user.friends=data.length;
          },
          error:err=>{
            console.log(err);
          }
        })
      },
      error: (err) => console.error('Failed to load user', err)
    });
  }

  loadPosts(): void {
    this.profileService.getUserPosts(this.currentUserId).subscribe({
      next: (posts) => {
        this.posts = (posts || []).map((p: any) => ({
          ...p,
          createdAt:p.createdAt,
          postId: p.postId,
          content: p.content || '',
          mediaUrl: p.mediaUrl|| null,
          mediaType: p.mediaType,
          likedUsers: [],
          comments: [],
          showComments: false,
          showLikes: false
        }));
        this.postCount=this.posts.length;

        this.posts.forEach(p => {
          this.loadLikes(p);
          this.loadComments(p);
        });
      },
      error: (err) => console.error('Error fetching posts', err)
    });
  }

  loadLikes(post: any): void {
    this.profileService.getLikesByPost(post.postId).subscribe({
      next: (likes) => {
        post.likedUsers = (likes || []).map(l => l.fullName || (l.userId ? `User ${l.userId}` : 'Unknown'));
      },
      error: () => post.likedUsers = []
    });
  }

  toggleLikes(post: any) {
    post.showLikes = !post.showLikes;
    if (post.showLikes) this.loadLikes(post);
  }

  likePost(post: any): void {
    const dto = { postId: post.postId, userId: this.currentUserId };
    this.profileService.likePost(dto).subscribe({
      next: () => this.loadLikes(post),
      error: (err) => console.error('Error liking post', err)
    });
  }

  viewComments(post: any): void {
    post.showComments = !post.showComments;
    if (post.showComments) this.loadComments(post);
  }

  loadComments(post: any): void {
    this.profileService.getComments(post.postId).subscribe({
      next: (comments) => {
        post.comments = (comments || []).map((c: any) => ({
          createdAt:c.createdAt,
          authorName: c.username || `User ${c.userId}`,
          text: c.content
        }));
      },
      error: () => post.comments = []
    });
  }

  //  Step 1: Ask confirmation via toast
  confirmDelete(postId: number): void {
    this.pendingDeleteId = postId;
    this.showToaster('Click again to confirm delete', 'info');
  }

  //  Step 2: Delete if confirmed
  deletePost(postId: number): void {
    if (this.pendingDeleteId !== postId) {
      this.confirmDelete(postId);
      return;
    }

    this.profileService.deletePost(postId).subscribe({
      next: () => {
        this.posts = this.posts.filter(p => p.postId !== postId);
        this.postCount = this.posts.length;
        this.pendingDeleteId = null;
        this.showToaster('Post deleted successfully', 'success');
      },
      error: err => {
        this.showToaster('Failed to delete post', 'error');
        console.log(err);
      }
    });
  }

  editPost(post: any): void {
    this.router.navigate(['/profile/upload', post.postId]);
  }
}