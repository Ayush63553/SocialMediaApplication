import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FriendsService, Request, UserDTO } from 'src/app/core/services/Friends/friends.service';

@Component({
  selector: 'app-friend-request-sent',
  templateUrl: './friend-request-sent.component.html',
  styleUrls: ['./friend-request-sent.component.css']
})
export class FriendRequestSentComponent implements OnInit {

  sentRequests: Request[] = [];
  expanded: { [userId: number]: boolean } = {};
  mutualFriendsMap: { [userId: number]: UserDTO[] } = {};
  ObjectKeys = Object.keys;

  searchText: string = "";
  filteredRequests: Request[] = [];

  //Toast state
  toastMessage: string = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast: boolean = false;

  constructor(private friendsService: FriendsService, private router: Router, private cdRef: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  // Show toast
  showToaster(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    this.cdRef.detectChanges();
    setTimeout(() => {
      this.showToast = false;
      this.cdRef.detectChanges();
    }, 3000);
  }

  loadRequests(): void {
    this.friendsService.getFriendRequestsSent(localStorage.getItem('userId')).subscribe({
      next: data => {
        this.sentRequests = data;
        this.filteredRequests = data;
        for (let i of this.sentRequests) {
          this.friendsService.findMutualFriend(localStorage.getItem("userId"), i.receiver.userId).subscribe({
            next: (data) => {
              this.mutualFriendsMap[i.receiver.userId] = data;
            },
            error: (err) => console.error('Error loading mutual friends', err)
          });
        }
      },
      error: err => console.error('Error loading sent requests', err)
    });
  }

  cancel(id: number): void {
    this.friendsService.cancelFriendRequest(localStorage.getItem('userId'), id).subscribe({
      next: data => {
        this.showToaster(data, 'success');
        this.loadRequests();
      },
      error: err => {
        this.showToaster("Cancel Request Failed", 'error');
        console.log(err);
      }
    });
  }

  onSearchChange(): void {
    const text = this.searchText.toLowerCase();
    this.sentRequests = this.filteredRequests.filter(request =>
      request.receiver.username.toLowerCase().includes(text)
    );
  }

  viewProfile(friend: UserDTO): void {
    this.showToaster(`Viewing profile of ${friend.username}`, 'info');
    this.router.navigate(['friends/profile', friend.userId]);
  }

  toggleDropdown(friendId: number): void {
    this.expanded[friendId] = !this.expanded[friendId];
  }
}