import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminRestaurantsComponent } from './admin-restaurants.component';

describe('AdminRestaurantsComponent', () => {
  let component: AdminRestaurantsComponent;
  let fixture: ComponentFixture<AdminRestaurantsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminRestaurantsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AdminRestaurantsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
