import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FindFriendsComponent } from './find-friends/find-friends.component';
import { FriendListComponent } from './friend-list/friend-list.component';
import { FriendsComponent } from './friends.component';
import { FriendRequestReceivedComponent } from './friend-request-received/friend-request-received.component';
import { FriendRequestSentComponent } from './friend-request-sent/friend-request-sent.component';
import { ViewPersonProfileComponent } from './view-person-profile/view-person-profile.component';
import {BlockListComponent} from './block-list/block-list.component';

const routes: Routes = [
    {path:'', component:FriendsComponent,
    children:[
      {path:'find', component: FindFriendsComponent},
      {path:'list',component:FriendListComponent},
      {path:'requests/received',component:FriendRequestReceivedComponent},
      {path:'requests/sent',component:FriendRequestSentComponent},
      {path:'profile/:userId',component:ViewPersonProfileComponent},
      {path:'blockList', component: BlockListComponent},
      {path:'', redirectTo:'find',pathMatch:'full'}
    ]}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FriendsRoutingModule { }
