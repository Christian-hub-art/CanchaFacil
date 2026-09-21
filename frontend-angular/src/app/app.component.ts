import { Component } from '@angular/core';
import { Espacios } from './components/espacios/espacios.component';

@Component({
  selector: 'app-root',
  imports: [Espacios],
  styleUrl: './app.component.css',
  templateUrl: './app.component.html',
})
export class App {}
