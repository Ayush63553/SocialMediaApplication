import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/services/Login/authLogin.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'facebook_frontend_group3';
  constructor(private authService:AuthService){}

  ngOnInit(): void {
      const token=this.authService.getToken();
      if(token) (this.authService as any).startAutoLogoutTimer(token);
  }
}
