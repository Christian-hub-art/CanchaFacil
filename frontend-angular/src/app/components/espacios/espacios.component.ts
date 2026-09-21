import { Component } from '@angular/core';

interface Espacio {
  id: number;
  nombre: string;
  tipoDeporte: string;
  precioHora: number;
  capacidad: number;
  negocio: string;
  disponible: boolean;
}

@Component({
  selector: 'app-espacios',
  imports: [],
  styleUrl: './espacios.component.css',
  templateUrl: './espacios.component.html',
})
export class Espacios {
  titulo = 'Canchas cerca de ti';
  filtro = 'Todas';

  espacios: Espacio[] = [
    {
      id: 1,
      nombre: 'Cancha Central',
      tipoDeporte: 'Futbol 5',
      precioHora: 50000,
      capacidad: 10,
      negocio: 'Sport Arena',
      disponible: true,
    },
    {
      id: 2,
      nombre: 'Arena Norte',
      tipoDeporte: 'Futbol 8',
      precioHora: 70000,
      capacidad: 12,
      negocio: 'Club Deportivo Norte',
      disponible: true,
    },
    {
      id: 3,
      nombre: 'Estadio Sur',
      tipoDeporte: 'Futbol 11',
      precioHora: 120000,
      capacidad: 22,
      negocio: 'Cancha Sur',
      disponible: false,
    },
  ];
}
