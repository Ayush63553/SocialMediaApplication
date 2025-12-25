import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { FriendsService, UserDTO } from 'src/app/core/services/Friends/friends.service';

@Component({
  selector: 'app-block-list',
  templateUrl: './block-list.component.html',
  styleUrls: ['./block-list.component.css']
})
export class BlockListComponent implements OnInit {
  allBlockedUsers: UserDTO[] = [];
  filteredBlockedUsers: UserDTO[] = [];
  searchText: string = '';

  extended: { [userId: number]: boolean } = {};
  mutualFriendsMap: { [userId: number]: UserDTO[] } = {}; // store loaded lists
  
  // Toast state
  toastMessage: string = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast: boolean = false;

  constructor(
    private friendsService: FriendsService,
    private router: Router,
    private cdRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadBlockedUsers();
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

  viewProfile(blockedUser: UserDTO): void {
    this.showToaster(`👤 Viewing profile of ${blockedUser.username}`, 'info');
    this.router.navigate(['friends/profile', blockedUser.userId]);
  }

  loadBlockedUsers(): void {
    this.friendsService.getBlockedUsers(localStorage.getItem("userId")).subscribe({
      next: (data) => {
        this.filteredBlockedUsers = data;  
        this.allBlockedUsers = data; 
        for (let i of this.filteredBlockedUsers) {
          this.friendsService.findMutualFriend(localStorage.getItem("userId"), i.userId).subscribe({
            next: (data) => {
              this.mutualFriendsMap[i.userId] = data;
            },
            error: (error) => {
              console.error('Error fetching mutual friends:', error);
            }
          });
        }
      },
      error: (error) => {
        console.error('Error fetching blocked users:', error);
      }
    });
  }

  OnSearchChange(): void {
    const text = this.searchText.toLowerCase();
    this.filteredBlockedUsers = this.allBlockedUsers.filter(user =>
      user.username.toLowerCase().includes(text)
    );
  }
  
  toggleDropdown(userId: number): void {
    this.extended[userId] = !this.extended[userId];
  }

  unBlockUser(blockedUser: UserDTO): void {
    this.friendsService.unBlockUser(localStorage.getItem("userId"), blockedUser.userId).subscribe({
      next: () => {
        this.showToaster(`✅ Unblocked ${blockedUser.username}`, 'success');
        this.filteredBlockedUsers = this.filteredBlockedUsers.filter(f => f.userId !== blockedUser.userId);
      
        // this.loadBlockedUsers(); // Refresh the block list
      },
      error: (error) => {
        console.error('Error unblocking user:', error);
        this.showToaster(`❌ Failed to unblock ${blockedUser.username}`, 'error');
      }
    });
  }

  ObjectKeys=Object.keys;
}
