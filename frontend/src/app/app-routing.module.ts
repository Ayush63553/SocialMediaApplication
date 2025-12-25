import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfileModule } from './features/profile/profile.module';
import { FriendsModule } from './features/friends/friends.module';
import { NotificationsModule } from './features/notifications/notifications.module';
import { HomeModule } from './features/home/home.module';
import { AuthModule } from './features/auth/auth.module';
import { FeedComponent } from './features/home/feed/feed.component';
import { FriendsComponent } from './features/friends/friends.component';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { LoginGuard } from './core/guards/login.guard';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full'},
  { path: 'auth', loadChildren:()=>import('./features/auth/auth.module').then(m=>m.AuthModule), canActivate:[LoginGuard]},
  { path: 'home', loadChildren:()=>import('./features/home/home.module').then(m=>m.HomeModule), canActivate:[AuthGuard]},
  { path: 'profile', loadChildren:()=>import('./features/profile/profile.module').then(m=>m.ProfileModule), canActivate:[AuthGuard]},
  { path: 'friends', loadChildren:()=>import('./features/friends/friends.module').then(m=>m.FriendsModule), canActivate:[AuthGuard]},
  { path: 'notifications', loadChildren:()=>import('./features/notifications/notifications.module').then(m=>m.NotificationsModule), canActivate:[AuthGuard]},
  { path: '**', redirectTo: 'home', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
