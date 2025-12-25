import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, LoginPayload } from 'src/app/core/services/Login/authLogin.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  credentails: LoginPayload = {
    username: '',
    password: ''
  };

  toastMessage: string = '';
  toastType: 'success' | 'error' = 'success';
  showToast: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.authService.login(this.credentails).subscribe({
      next: (res) => {
        this.showToaster('Login Successful ✅', 'success');
        console.log(res);
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 1000);
      },
      error: (err) => {
        this.showToaster('❌ Invalid Credentials', 'error');
        console.log(err);
      }
    });
  }

  showToaster(message: string, type: 'success' | 'error') {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;

    setTimeout(() => {
      this.showToast = false;
    }, 3000);
  }
}