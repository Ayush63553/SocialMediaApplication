import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User, UserService } from '../user.service';
import { AuthService } from 'src/app/core/services/Login/authLogin.service';

type EnabledField = 'username' | 'dateOfBirth' | 'bio';

@Component({
  selector: 'app-profile-edit',
  templateUrl: './profile-edit.component.html',
  styleUrls: ['./profile-edit.component.css']
})
export class ProfileEditComponent implements OnInit {
  userId!: number;
  username = '';
  dateOfBirth = '';
  bio = '';
  avatar: string | null = null;

  selectedFile: File | null = null;

  editMode: Record<EnabledField, boolean> = { username: false, dateOfBirth: false, bio: false };

  // Toast state
  toastMessage: string = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast: boolean = false;

  constructor(
    private router: Router,
    private userService: UserService,
    private authService: AuthService
  ) {
    this.userId = this.authService.getUserId();
  }

  ngOnInit() {
    this.userService.getUser(this.userId).subscribe((data: User) => {
      this.username = data.username ?? '';
      this.dateOfBirth = data.dateOfBirth ?? '';
      this.bio = data.bio ?? '';
      this.avatar = data.profilePictureUrl ?? null;
    });
  }

  toggleEdit(field: keyof typeof this.editMode) {
    this.editMode[field] = !this.editMode[field];
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.avatar = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  // Toast function
  showToaster(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }

  removePicture() {
    this.userService.removeProfilePhoto(this.userId).subscribe({
      next: () => {
        this.avatar = null;
        this.selectedFile = null;
        this.showToaster('Profile photo removed successfully', 'success');
      },
      error: (err) => {
        console.error("Error removing the photo", err);
        this.showToaster("Failed to remove the photo", 'error');
      }
    });
  }

  saveProfile() {
    const formData = new FormData();
    if(this.username){
      formData.append('username', this.username);
    }
    if(this.dateOfBirth){
      formData.append('dateOfBirth', this.dateOfBirth);
    }
    if(this.bio){
      formData.append('bio', this.bio);
    }
    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }

    this.userService.updateUser(this.userId, formData).subscribe({
      next: () => {
        this.showToaster('Profile updated successfully!', 'success');
        setTimeout(()=> this.router.navigate(['/profile']), 1500);
      },
      error: (err) => {
        console.error('Error updating profile', err);
        this.showToaster('Failed to update profile!', 'error');
      }
    });
  }
}