import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FriendsService, UserDTO } from 'src/app/core/services/Friends/friends.service';
import { PrivacyService } from 'src/app/core/services/Privacy/privacy.service';
import { ProfileService } from 'src/app/main/services/profile.service';

@Component({
  selector: 'app-upload-post',
  templateUrl: './upload-post.component.html',
  styleUrls: ['./upload-post.component.css']
})
export class UploadPostComponent implements OnInit {
  mediaPreview: string | ArrayBuffer | null = null;
  description: string = '';
  privacy: string = "PUBLIC";
  postId: number | null = 0;   
  isEdit: boolean = false;
  currentUserId: number;
  selectedFile: File | null = null;

  friends: UserDTO[] = [];
  isCustomPrivacy: boolean = false;

  // ✅ Toast state
  toastMessage: string = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private profileService: ProfileService,
    private friendsService: FriendsService,
    private privacyService: PrivacyService,
  ) {
    this.currentUserId = Number(localStorage.getItem('userId')) || 1;
  }

  ngOnInit(): void {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    this.isEdit = !!this.postId;

    if (this.isEdit && this.postId) {
      this.profileService.getPostById(this.postId).subscribe({
        next: (post) => {
          this.description = post.content;
          this.privacy = post.privacy;
          this.mediaPreview = post.mediaUrl || null;
          if (this.privacy === 'CUSTOM') this.isCustomPrivacy = true;
          this.loadRequests();
        },
        error: (err) => console.error('Error loading post', err)
      });
    }
  }
  checkFunction(url:any):Boolean{
    if(!url) return false;

    if(typeof url == 'string' && url.startsWith('data:')) {
      return url.startsWith('data:image');
    }
    const pattern = /\.(png|jpg|jpeg|gif|webp)$/i;
    return pattern.test(url);
  }
  // Toast function
  showToaster(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }

  onMediaSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = () => {
        this.mediaPreview = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }
  isImage(file: any) : boolean {

    return typeof file === 'string' && file.includes('/uploads') || (typeof file === 'string' && file.startsWith('image/'));
  }

  isVideo(file: any) :boolean{
    return typeof file === 'string' && file.includes('/uploads') || (typeof file === 'string' && file.startsWith('video/'));
  }

  savePost() {
    if (!this.description.trim() && !this.selectedFile) {
      this.showToaster('Post cannot be empty', 'error');
      return;
    }

    const formData = new FormData();
    formData.append('content', this.description);
    formData.append('privacy', this.privacy);

    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }

    if (this.isEdit && this.postId) {
      this.profileService.updatePost(this.postId, formData).subscribe({
        next: () => {
          this.showToaster('Post updated successfully', 'success');
          if (this.isCustomPrivacy) this.saveCustomPrivacy(this.postId);
          setTimeout(() => this.router.navigate(['/profile']), 1500);
        },
        error: () => this.showToaster('Failed to update post', 'error')
      });
    } else {
      this.profileService.createPost(this.currentUserId, formData).subscribe({
        next: data => {
          this.showToaster('Post created successfully', 'success');
          if (this.isCustomPrivacy){
            this.saveCustomPrivacy(data.postId);
          }
          setTimeout(() => this.router.navigate(['/profile']), 1500);
        },
        error: () => this.showToaster('Failed to create post', 'error')
      });
    }
  }

  cancelPost() {
    this.router.navigate(['/profile']);
  }

  loadRequests(): void {
    this.friendsService.getFriends(localStorage.getItem('userId')).subscribe({
      next: data => {
        this.friends = data.map(friend => {
          friend.selected = false;
          return friend;
        });
        if (this.postId !== 0) {
          this.privacyService.getRules(this.postId).subscribe({
            next: d => {
              d.forEach(x => {
                this.friends.map(friend => {
                  if (x.userId == friend.userId) {
                    friend.selected = true;
                  }
                });
              });
            },
            error: err => console.log("Error occured: ", err)
          });
        }
      },
      error: err => console.error('Error loading received requests', err)
    });
  }

  check(friendId: any): void {
    this.friends[friendId].selected = !this.friends[friendId].selected;
  }

  onPrivacyChange(event: any) {
    this.isCustomPrivacy = this.privacy === "CUSTOM";
    this.loadRequests();
  }

  saveCustomPrivacy(postId: number | null): void {
    this.privacyService.deleteRules(postId).subscribe({
      next: () => {
        const selectedFriends = this.friends.filter(f => f.selected);
        selectedFriends.forEach(friend =>
          this.privacyService.addRule(postId, friend.userId).subscribe({
            next: data => console.log(data),
            error: err => console.log("Error:", err)
          })
        );
      },
      error: err => console.log("Error occured:", err)
    });
  }
}
