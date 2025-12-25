import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})

export class SidebarComponent {
  constructor(private router: Router) {}
  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  requestsExpanded=false;
  toggleRequests():void{
    this.requestsExpanded=!this.requestsExpanded;
  }

}