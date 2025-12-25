import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewPersonProfileComponent } from './view-person-profile.component';

describe('ViewPersonProfileComponent', () => {
  let component: ViewPersonProfileComponent;
  let fixture: ComponentFixture<ViewPersonProfileComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ViewPersonProfileComponent]
    });
    fixture = TestBed.createComponent(ViewPersonProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
