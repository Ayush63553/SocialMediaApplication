import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendRequestReceivedComponent } from './friend-request-received.component';

describe('FriendRequestReceivedComponent', () => {
  let component: FriendRequestReceivedComponent;
  let fixture: ComponentFixture<FriendRequestReceivedComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FriendRequestReceivedComponent]
    });
    fixture = TestBed.createComponent(FriendRequestReceivedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
