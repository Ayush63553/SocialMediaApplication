import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Friend, FriendsService, UserDTO } from 'src/app/core/services/Friends/friends.service';
import { ProfileService } from 'src/app/main/services/profile.service';

@Component({
  selector: 'app-view-person-profile',
  templateUrl: './view-person-profile.component.html',
  styleUrls: ['./view-person-profile.component.css']
})

export class ViewPersonProfileComponent implements OnInit{
  userId!: string;
  profile: UserDTO={
    userId:0,
    username:'',
    bio:'',
    profilePictureUrl:'',
    email:'',
    firstName:'',
    lastName:'',
    dateOfBirth:new Date(),
    gender:'',
    relationshipStatus:''
  };

  loading = true;
  profileCheck=false;

  constructor(private route: ActivatedRoute, private friendsService: FriendsService,
    private profileService:ProfileService,
    private router:Router) {}

  ngOnInit(): void {

    this.route.paramMap.subscribe(params => {

      this.userId = params.get('userId')!;

      this.fetchProfile();
      

    });

  }

  checkStatus():void{
    this.friendsService.getStatus(localStorage.getItem('userId'),this.profile.userId).subscribe({
      next: data=>this.profile.relationshipStatus=data,
      error:err=>console.log('error occured:',err)
    })
  }

  checkId:string|null="";
  isBlocked:boolean=false;
  fetchProfile(): void {
    this.friendsService.checkIfBlockedUserExists(localStorage.getItem('userId'),this.userId).subscribe({
      next:data=>{
        this.isBlocked=data;
      },
      error:err=>{
        console.log('Error checking blocked status',err);
        this.isBlocked=false;
      }
    });
    this.friendsService.getProfile(localStorage.getItem('userId'),this.userId).subscribe({
      next: (data) => {
        this.checkId=data.userId.toString();
        if(this.checkId==localStorage.getItem('userId')){
          this.router.navigate(['/profile']);
        }
        this.profile = data;
        this.loading = false;
        this.profileCheck=true;
        this.checkStatus();
        this.fetchMutualFriends();
        this.fetchPosts();
      },
      error: (err) => {
        console.error('Error fetching profile', err);
        this.loading = false;
        this.profileCheck=false;
      }
    });

  }
  
  mutualFriends:UserDTO[]=[];
  toggleFriends:boolean=false;

  toggle(){
    this.toggleFriends=!this.toggleFriends;
    if(this.check) this.fetchMutualFriends();
  }
  check:boolean=true;
  fetchMutualFriends(){
    this.check=false;    
    this.friendsService.findMutualFriend(localStorage.getItem("userId"),this.profile.userId).subscribe({
          next: (data) => {
            this.mutualFriends =data;
          },
          error: (err) => console.error('Error loading mutual friends', err)
          });
  }

  addFriend(): void {
    this.friendsService.sendFriendRequest(localStorage.getItem('userId'),this.profile.userId).subscribe(() => {
      this.profile.relationshipStatus = 'REQUEST_SENT';
    });
  }

  unfriend(): void {
    this.friendsService.unFriend(localStorage.getItem('userId'),this.profile.userId).subscribe(() => {
      this.profile.relationshipStatus = 'NOT_FRIEND';
    });
  }

  acceptRequest(): void {
    this.friendsService.acceptFriendRequest(this.profile.userId,localStorage.getItem('userId')).subscribe(() => {
      this.profile.relationshipStatus = 'FRIEND';
    });
  }

  rejectRequest():void{
    this.friendsService.rejectFriendRequest(this.profile.userId,localStorage.getItem('userId')).subscribe(()=>{
      this.profile.relationshipStatus='NOT_FRIEND'; 
    })
  }

  cancelRequest():void{
    this.friendsService.cancelFriendRequest(localStorage.getItem('userId'),this.profile.userId).subscribe(()=>{
      this.profile.relationshipStatus='NOT_FRIEND'; 
    })
  }

  
  goToMutualFriendProfile(userId:any){
    this.check=true;
    this.toggleFriends=false;
    this.router.navigate(['friends/profile',userId]);   
  }

  posts:any[]=[];
  fetchPosts():void{
    this.profileService.getUserProfilePosts(localStorage.getItem('userId'),this.profile.userId).subscribe({
      next:data=>{
        this.posts = (data || []).map((p: any) => ({
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
        
        this.posts.forEach(p => {
          this.loadLikes(p);
          this.loadComments(p);
        });
        // console.log(this.posts);
      },
      error:err=>{
        console.log("Error occured",err);
      }
    })
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
    const dto = { postId: post.postId, userId: this.profile.userId };
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

  blockUser(): void {
    this.friendsService.blockUser(localStorage.getItem('userId'), this.profile.userId).subscribe({
      next: ()=> {
        this.isBlocked=true;
      },
      error: err=>console.log('Error blocking user',err)
    });
  }
  
  unblockUser(): void {
    this.friendsService.unBlockUser(localStorage.getItem('userId'), this.profile.userId).subscribe(() => {
      this.isBlocked = false;
    });
  }
}