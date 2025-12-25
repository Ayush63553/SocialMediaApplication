import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FriendsRoutingModule } from './friends-routing.module';
import { FindFriendsComponent } from './find-friends/find-friends.component';
import { FriendListComponent } from './friend-list/friend-list.component';
import { FriendsComponent } from './friends.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { FormsModule } from '@angular/forms';
import { FriendRequestReceivedComponent } from './friend-request-received/friend-request-received.component';
import { FriendRequestSentComponent } from './friend-request-sent/friend-request-sent.component';
import { ViewPersonProfileComponent } from './view-person-profile/view-person-profile.component';
import { BlockListComponent } from './block-list/block-list.component';


@NgModule({
  declarations: [
    FindFriendsComponent,
    FriendListComponent,
    FriendsComponent,
    FriendRequestReceivedComponent,
    FriendRequestSentComponent,
    ViewPersonProfileComponent,
    BlockListComponent
  ],
  imports: [
    CommonModule,
    FriendsRoutingModule,
    SharedModule,
    FormsModule
  ]
})
export class FriendsModule { }
