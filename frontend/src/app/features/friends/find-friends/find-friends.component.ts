import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { FriendsService, UserDTO } from 'src/app/core/services/Friends/friends.service';

@Component({
  selector: 'app-find-friends',
  templateUrl: './find-friends.component.html',
  styleUrls: ['./find-friends.component.css']
})
export class FindFriendsComponent implements OnInit {

  allFriends: UserDTO[] = [];
  filteredFriends: UserDTO[] = [];
  searchText: string = '';

  expanded: { [userId: number]: boolean } = {};
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
    this.loadFriends();
    // console.log(localStorage.getItem('userId'));
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

  viewProfile(friend: UserDTO): void {
    this.showToaster(`👤 Viewing profile of ${friend.username}`, 'info');
    this.router.navigate(['friends/profile', friend.userId]);
  }

  loadFriends(): void {
    this.friendsService.findFriends(localStorage.getItem("userId")).subscribe({
      next: (data) => {
        this.filteredFriends = data;  
        this.allFriends = data; 
        for (let i of this.filteredFriends) {
          this.friendsService.findMutualFriend(localStorage.getItem("userId"), i.userId).subscribe({
            next: (data) => {
              this.mutualFriendsMap[i.userId] = data;
            },
            error: (err) => console.error('Error loading mutual friends', err)
          });    
        }
      },
      error: (err) => console.error('Error loading friends', err)
    });
  }

  onSearchChange(): void {
    const text = this.searchText.toLowerCase();
    this.filteredFriends = this.allFriends.filter(friend =>
      friend.username.toLowerCase().includes(text)
    );
  }

  toggleDropdown(friendId: number): void {
    this.expanded[friendId] = !this.expanded[friendId];
  }

  sendRequest(friend: UserDTO): void {
    this.friendsService.sendFriendRequest(localStorage.getItem('userId'), friend.userId).subscribe({
      next: () => {
        this.showToaster(`✅ Friend request sent to ${friend.username}`, 'success');
        this.filteredFriends = this.filteredFriends.filter(f => f.userId !== friend.userId);
      },
      error: (err) => {
        this.showToaster('❌ Failed to send request', 'error');
        console.error(err);
      }
    });
  }

  ObjectKeys = Object.keys;
}