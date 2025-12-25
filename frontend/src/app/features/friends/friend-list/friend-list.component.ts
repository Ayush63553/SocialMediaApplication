import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { FriendsService, UserDTO } from 'src/app/core/services/Friends/friends.service';

@Component({
  selector: 'app-friend-list',
  templateUrl: './friend-list.component.html',
  styleUrls: ['./friend-list.component.css']
})
export class FriendListComponent implements OnInit {
  
  friends: UserDTO[] = [];
  searchText: string = '';
  filteredFriends: UserDTO[] = [];

  expanded: { [userId: number]: boolean } = {};
  mutualFriendsMap: { [userId: number]: UserDTO[] } = {}; // store loaded lists

  ObjectKeys = Object.keys;

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
    this.loadRequests();
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

  onSearchChange(): void {
    const text = this.searchText.toLowerCase();
    this.friends = this.filteredFriends.filter(friend =>
      friend.username.toLowerCase().includes(text)
    );
  }

  loadRequests(): void {
    this.friendsService.getFriends(localStorage.getItem('userId')).subscribe({
      next: data => {
        this.friends = data;
        this.filteredFriends = data;
        for (let i of this.filteredFriends) {
          this.friendsService.findMutualFriend(localStorage.getItem("userId"), i.userId).subscribe({
            next: (data) => {
              this.mutualFriendsMap[i.userId] = data;
            },
            error: (err) => console.error('Error loading mutual friends', err)
          });    
        }
      },
      error: err => console.error('Error loading received requests', err)
    });
  }

  toggleDropdown(friendId: number): void {
    this.expanded[friendId] = !this.expanded[friendId];
  }

  viewProfile(friend: UserDTO): void {
    this.showToaster(`👤 Viewing profile of ${friend.username}`, 'info');
    this.router.navigate(['friends/profile', friend.userId]);
  }

  unfriend(friend: UserDTO): void {
    this.friendsService.unFriend(localStorage.getItem('userId'), friend.userId).subscribe({
      next: data => {
        this.showToaster(`🗑 ${data}`, 'success');
        this.loadRequests();
      },
      error: () => this.showToaster("❌ Friend couldn't be deleted", 'error')
    });
  }
}