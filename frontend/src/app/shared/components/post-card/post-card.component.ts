import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-post-card',
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.css']
})
export class PostCardComponent {

  @Input() post: any;

  likeCount = 0;

  likePost() {
    this.likeCount++;
  }

  commentPost() {
    alert("Comment feature coming soon!");
  }
}
