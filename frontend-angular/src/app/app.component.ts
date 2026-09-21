import { Component } from '@angular/core';
import { ReservasComponent } from './components/reservas/reservas.component';

@Component({
  selector: 'app-root',
  imports: [ReservasComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class App {}
