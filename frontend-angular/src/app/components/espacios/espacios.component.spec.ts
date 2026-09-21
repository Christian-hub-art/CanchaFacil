import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Espacios } from './espacios.component';

describe('Espacios', () => {
  let component: Espacios;
  let fixture: ComponentFixture<Espacios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Espacios],
    }).compileComponents();

    fixture = TestBed.createComponent(Espacios);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the title and sample courts', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h2')?.textContent).toContain('Canchas cerca de ti');
    expect(compiled.querySelectorAll('.tarjeta-cancha').length).toBeGreaterThan(2);
  });
});
