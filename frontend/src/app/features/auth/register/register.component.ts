import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, RegisterPayload } from 'src/app/core/services/Register/auth.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  firstName: string = '';
  surname: string = '';
  contact: string = '';
  email: string = '';
  username: string = '';
  password: string = '';
  confirmPassword: string = '';
  day: number | null = null;
  month: number | null = null;
  year: number | null = null;
  gender: string = 'Female';
  days: number[] = [];

  securityQuestion: string = '';
  securityAnswer: string = '';

  months: { name: string, value: number }[] = [
    { name: 'Jan', value: 1 }, { name: 'Feb', value: 2 }, { name: 'Mar', value: 3 },
    { name: 'Apr', value: 4 }, { name: 'May', value: 5 }, { name: 'Jun', value: 6 },
    { name: 'Jul', value: 7 }, { name: 'Aug', value: 8 }, { name: 'Sep', value: 9 },
    { name: 'Oct', value: 10 }, { name: 'Nov', value: 11 }, { name: 'Dec', value: 12 }
  ];
  years: number[] = [];

  // Toaster state (your HTML already uses these)
  toastMessage: string = '';
  toastType: 'success' | 'error' | 'info' = 'info';
  showToast: boolean = false;

  // DOB error flag used in template via *ngIf="dobError"
  dobError: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    for (let i = 1; i <= 31; i++) {
      this.days.push(i);
    }
    const currentYear = new Date().getFullYear();
    for (let i = currentYear; i >= currentYear - 100; i--) {
      this.years.push(i);
    }

    // default picks to avoid template undefined issues
    if (!this.day) this.day = 1;
    if (!this.month) this.month = 1;
    if (!this.year) this.year = currentYear - 25;
  }

  user: RegisterPayload = {
    firstName: '', lastName: '', email: '', username: '', password: '',
    dateOfBirth: '', gender: '', profilePictureUrl: '', bio: ''
  };

  // show toaster helper
  showToaster(message: string, type: 'success' | 'error' | 'info' = 'info') {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    // auto-hide
    setTimeout(() => this.showToast = false, 4000);
  }

  // check age >= 13
  private isOldEnough(): boolean {
    if (!this.day || !this.month || !this.year) return false;
    const dob = new Date(this.year, this.month - 1, this.day);
    const today = new Date();
    // user must be born on or before this date
    const minAllowed = new Date(today.getFullYear() - 13, today.getMonth(), today.getDate());
    return dob <= minAllowed;
  }

  validateDOB() {
    this.dobError = !this.isOldEnough();
  }

  // attempt to extract useful message(s) from various backend error shapes
  private extractErrorMessage(err: any): string {
    // If HttpErrorResponse, try to parse err.error in many ways
    if (err instanceof HttpErrorResponse) {
      const status = err.status;
      const statusText = err.statusText || '';

      // If network / CORS / no response
      if (status === 0) {
        return 'Network error or server unreachable';
      }

      let payload = err.error;

      // payload can be string (HTML or plain text) or object
      if (typeof payload === 'string') {
        // try parsing JSON string first
        try {
          payload = JSON.parse(payload);
        } catch {
          // not JSON -> use string
          const txt = payload.trim();
          if (txt) return this.mapToFriendly(txt);
        }
      }

      // If payload is an object, look for common fields
      if (payload && typeof payload === 'object') {
        // common spring boot / custom shapes
        const candidates: string[] = [];

        if (payload.message && typeof payload.message === 'string') candidates.push(payload.message);
        if (payload.error && typeof payload.error === 'string') candidates.push(payload.error);
        if (payload.detail && typeof payload.detail === 'string') candidates.push(payload.detail);

        // validation errors array (e.g. BindingResult)
        if (payload.errors && Array.isArray(payload.errors)) {
          payload.errors.forEach((e: any) => {
            if (typeof e === 'string') candidates.push(e);
            else if (e.defaultMessage) candidates.push(e.defaultMessage);
            else if (e.message) candidates.push(e.message);
            else candidates.push(JSON.stringify(e));
          });
        }

        // sometimes message nested: err.error.error.message or err.error.data.message
        if (payload.data && typeof payload.data === 'object') {
          if (payload.data.message) candidates.push(payload.data.message);
        }
        if (payload.error && typeof payload.error === 'object') {
          if (payload.error.message) candidates.push(payload.error.message);
        }

        // if we collected candidates return the mapped friendly message
        if (candidates.length > 0) {
          const combined = candidates.join(' | ');
          return this.mapToFriendly(combined);
        }

        // fallback to stringify object
        try {
          const raw = JSON.stringify(payload);
          return this.mapToFriendly(raw);
        } catch {
          return 'Registration Failed';
        }
      }

      // Last resort fallback
      return statusText ? this.mapToFriendly(statusText) : `Error ${status}`;
    }

    // not HttpErrorResponse
    try {
      return this.mapToFriendly(String(err || 'Registration Failed'));
    } catch {
      return 'Registration Failed';
    }
  }

  // map a raw message to a friendly specific message when possible
  private mapToFriendly(rawMsg: string): string {
    const msg = rawMsg.toLowerCase();

    // detect username/email keywords and existence/duplicate mention
    const mentionsUsername = /username|user ?name|user.*name/i.test(msg);
    const mentionsEmail = /email|e-?mail/i.test(msg);
    const mentionsExists = /already exist|already exists|exists|duplicate|taken|unique/i.test(msg);

    if (mentionsUsername && mentionsExists) return 'Username already exists';
    if (mentionsEmail && mentionsExists) return 'Email already exists';

    // some backends return "User with email X already exists" -> check
    if (/user.*email.*exist/i.test(msg)) return 'Email already exists';
    if (/user.*username.*exist/i.test(msg)) return 'Username already exists';

    // if raw message contains both username & email and mentions existence, return both
    if (mentionsUsername && mentionsEmail && mentionsExists) return 'Username and Email already exist';

    // if we couldn't map specifically, return the trimmed backend message (if short) otherwise fallback
    const trimmed = rawMsg.trim();
    if (trimmed.length > 0 && trimmed.length < 250) return trimmed;
    return 'Registration Failed';
  }

  onRegister() {
    // client-side checks
    if (this.password !== this.confirmPassword) {
      this.showToaster("Password doesn't match!!", 'error');
      return;
    }

    this.validateDOB();
    if (this.dobError) {
      this.showToaster('You must be at least 13 years old to register.', 'error');
      return;
    }

    // build payload
    this.user = {
      firstName: this.firstName,
      lastName: this.surname,
      email: this.email,
      username: this.username,
      password: this.password,
      dateOfBirth: `${this.year}-${this.month}-${this.day}`,
      gender: this.gender,
      profilePictureUrl: './assets/profilePhoto.webp',
      bio: ''
    };

    this.authService.registerUser(this.user).subscribe({
      next: res => {
        this.showToaster('Registration Successful!', 'success');
        const userid = (res && (res as any).userId) ? (res as any).userId : null;

        // set security question if we have the user id
        const securityPayload = {
          userId: userid,
          question: this.securityQuestion?.trim(),
          answer: this.securityAnswer?.trim()
        };

        if (userid) {
          this.authService.setSecurityQuestion(securityPayload).subscribe({
            next: () => {
              this.showToaster('Security question saved', 'success');
              setTimeout(() => this.router.navigate(['/auth']), 1400);
            },
            error: secErr => {
              // security question save failed but registration succeeded — show friendly toast
              this.showToaster('User created but failed to save the security question', 'error');
              console.error('Security question error:', secErr);
              setTimeout(() => this.router.navigate(['/auth']), 1400);
            }
          });
        } else {
          // if backend didn't return userId, still navigate after short delay
          setTimeout(() => this.router.navigate(['/auth']), 1400);
        }
      },
      error: (err: HttpErrorResponse) => {
        // try to extract meaningful backend message
        const friendly = this.extractErrorMessage(err);
        this.showToaster(friendly, 'error');
        console.error('Registration error (raw):', err);
      }
    });
  }
}