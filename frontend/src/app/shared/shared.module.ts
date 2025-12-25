import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostCardComponent } from './components/post-card/post-card.component';
import { FooterComponent } from './components/footer/footer.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { RouterModule } from '@angular/router';



@NgModule({
  declarations: [
    PostCardComponent,
    FooterComponent,
    SidebarComponent
  ],
  imports: [
    CommonModule,RouterModule
  ],
  exports: [
    PostCardComponent,
    FooterComponent,
    SidebarComponent,RouterModule
  ]
})
export class SharedModule { }
