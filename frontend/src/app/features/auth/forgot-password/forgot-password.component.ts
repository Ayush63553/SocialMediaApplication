import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

const BASE = 'http://localhost:8765/api/auth';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  username = '';
  email = '';
  question = '';
  answer = '';
  newPassword = '';

  validated = false;
  verified = false;
  userId!: number;

  // toast state
  toastMessage: string = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast: boolean = false;

  constructor(private http: HttpClient, private router: Router) {}

  // Show toast function
  showToaster(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;

    setTimeout(() => {
      this.showToast = false;
    }, 3000);
  }

  // Step 1: validate user
  validateUser() {
    this.http.get<any>(`${BASE}/validate-user?username=${this.username}&email=${this.email}`)
      .subscribe({
        next: (res) => {
          this.userId = res.userId;
          this.validated = true;

          this.http.get(`${BASE}/get-question/${this.userId}`, { responseType: 'text' })
            .subscribe({
              next: (q) => this.question = q,
              error: () => this.showToaster("No security question found", 'error')
            });
        },
        error: () => this.showToaster("User not found with given username and email", 'error')
      });
  }

  // Step 2: verify answer
  verifyAnswer() {
    const payload = { userId: this.userId, question: this.question, answer: this.answer };
    this.http.post(`${BASE}/forgot-password`, payload, { responseType: 'text' })
      .subscribe({
        next: () => {
          this.verified = true;
          this.showToaster("Answer verified. Please reset your password.", 'success');
        },
        error: () => this.showToaster("Wrong answer!", 'error')
      });
  }

  // Step 3: reset password
  resetPassword() {
    const payload = { userId: this.userId, newPassword: this.newPassword };
    this.http.post(`${BASE}/reset-password`, payload, { responseType: 'text' })
      .subscribe({
        next: () => {
          this.showToaster("Password reset successful!", 'success');
          setTimeout(() => this.router.navigate(['/auth']), 1500);
        },
        error: () => this.showToaster("Failed to reset password", 'error')
      });
  }
}