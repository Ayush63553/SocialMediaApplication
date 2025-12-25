import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FriendsService, Request, UserDTO } from 'src/app/core/services/Friends/friends.service';
import { NotificationService } from 'src/app/core/services/notification.service';

@Component({
  selector: 'app-friend-request-received',
  templateUrl: './friend-request-received.component.html',
  styleUrls: ['./friend-request-received.component.css']
})
export class FriendRequestReceivedComponent implements OnInit {

  requests: Request[] = [];
  filteredRequests: Request[] = [];
  searchText: string = '';
  
  expanded: { [userId: number]: boolean } = {};
  mutualFriendsMap: { [userId: number]: UserDTO[] } = {};

  ObjectKeys = Object.keys;

  // ✅ toast state
  toastMessage: string = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast: boolean = false;

  constructor(
    private friendsService: FriendsService, 
    private router: Router,
    private notificationService: NotificationService,
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

  viewProfile(friend: UserDTO): void {
    this.router.navigate(['friends/profile', friend.userId]);
  }

  loadRequests(): void {
    this.friendsService.getFriendRequestsReceived(localStorage.getItem('userId')).subscribe({
      next: data => {
        this.requests = data;
        this.filteredRequests = data;
        for (let i of this.requests) {
          this.friendsService.findMutualFriend(localStorage.getItem("userId"), i.sender.userId).subscribe({
            next: (data) => {
              this.mutualFriendsMap[i.sender.userId] = data;
            },
            error: (err) => console.error('Error loading mutual friends', err)
          });    
        }
        this.notificationService.updateCount(this.requests.length);
      },
      error: err => console.error('Error loading received requests', err)
    });
  }

  onSearchChange(): void {
    const text = this.searchText.toLowerCase();
    this.requests = this.filteredRequests.filter(request =>
      request.sender.username.toLowerCase().includes(text)
    );
  }

  accept(senderId: number): void {
    this.friendsService.acceptFriendRequest(senderId, localStorage.getItem('userId')).subscribe({
      next: () => {
        this.requests = this.requests.filter(req => req.sender.userId !== senderId);
        this.filteredRequests = this.filteredRequests.filter(req => req.sender.userId !== senderId);
        this.notificationService.updateCount(this.requests.length);

        this.showToaster("✅ Request accepted", 'success');
      },
      error: () => this.showToaster("❌ Request can't be accepted", 'error')   
    });
  }

  reject(senderId: number): void {
    this.friendsService.rejectFriendRequest(senderId, localStorage.getItem('userId')).subscribe({
      next: () => {
        this.requests = this.requests.filter(req => req.sender.userId !== senderId);
        this.filteredRequests = this.filteredRequests.filter(req => req.sender.userId !== senderId);
        this.notificationService.updateCount(this.requests.length);

        this.showToaster("🚫 Request rejected", 'success');
      },
      error: () => this.showToaster("❌ Request can't be rejected", 'error')   
    });
  }

  toggleDropdown(friendId: number): void {
    this.expanded[friendId] = !this.expanded[friendId];
  }
}