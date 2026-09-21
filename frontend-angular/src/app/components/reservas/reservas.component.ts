import { Component } from '@angular/core';

@Component({
  imports: [],
  selector: 'app-reservas',
  styleUrl: './reservas.component.css',
  templateUrl: './reservas.component.html',
})
export class ReservasComponent {
  reservas = [
    {
      id: 1,
      usuario: 'Juan',
      espacio: 'Cancha 1',
      fecha: '2026-09-21',
      horaInicio: '18:00',
      horaFin: '19:00',
      estado: 'PENDIENTE',
    },
    {
      id: 2,
      usuario: 'Maria',
      espacio: 'Cancha 2',
      fecha: '2026-09-22',
      horaInicio: '10:00',
      horaFin: '11:00',
      estado: 'CONFIRMADA',
    },
    {
      id: 3,
      usuario: 'Carlos',
      espacio: 'Cancha 3',
      fecha: '2026-09-23',
      horaInicio: '15:00',
      horaFin: '16:00',
      estado: 'COMPLETADA',
    },
  ];
}
