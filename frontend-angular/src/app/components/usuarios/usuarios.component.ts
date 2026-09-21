import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface Usuario {
  id: number;
  nombre: string;
  email: string;
  password: string;
  telefono: string;
  direccion: string;
  rol: 'CLIENTE' | 'ADMINISTRADOR';
}

@Component({
  imports: [CommonModule, FormsModule],
  selector: 'app-usuarios',
  styleUrl: './usuarios.component.css',
  templateUrl: './usuarios.component.html',
})
export class Usuarios {
  protected readonly roles: Usuario['rol'][] = ['CLIENTE', 'ADMINISTRADOR'];
  protected usuarios: Usuario[] = [{
    id: 1, nombre: 'Juan Perez', email: 'juan@mail.com', password: '',
    telefono: '300 000 0000', direccion: 'Calle 10 # 20-30', rol: 'CLIENTE',
  }];
  
  protected nombreBusqueda = '';
  protected formularioVisible = false;
  protected usuarioSeleccionado: Usuario | null = null;
  protected usuarioEnDetalle: Usuario | null = null;
  protected error = '';

  protected get usuariosFiltrados(): Usuario[] {
    const termino = this.nombreBusqueda.trim().toLowerCase();
    return termino
      ? this.usuarios.filter((usuario) => usuario.nombre.toLowerCase().includes(termino))
      : this.usuarios;
  }

  protected nuevoUsuario(): void {
    this.usuarioSeleccionado = {
      id: 0, nombre: '', email: '', password: '', telefono: '', direccion: '', rol: 'CLIENTE',
    };
    this.error = '';
    this.formularioVisible = true;
  }

  protected editarUsuario(usuario: Usuario): void {
    this.usuarioSeleccionado = { ...usuario };
    this.error = '';
    this.formularioVisible = true;
  }

  protected guardarUsuario(): void {
    if (!this.usuarioSeleccionado) return;
    const usuario = this.usuarioSeleccionado;
    if (!usuario.nombre.trim() || !usuario.email.trim() || !usuario.direccion.trim()) {
      this.error = 'Completa los campos obligatorios.';
      return;
    }
    if (this.usuarios.some((actual) => actual.email === usuario.email && actual.id !== usuario.id)) {
      this.error = 'Ya existe un usuario con ese email.';
      return;
    }
    if (usuario.id === 0) {
      const nuevoId = Math.max(0, ...this.usuarios.map((actual) => actual.id)) + 1;
      this.usuarios = [...this.usuarios, { ...usuario, id: nuevoId }];
    } else {
      this.usuarios = this.usuarios.map((actual) => actual.id === usuario.id ? { ...usuario } : actual);
    }
    this.cancelarFormulario();
  }

  protected eliminarUsuario(usuario: Usuario): void {
    this.usuarios = this.usuarios.filter((actual) => actual.id !== usuario.id);
    if (this.usuarioEnDetalle?.id === usuario.id) this.usuarioEnDetalle = null;
  }

  protected verUsuario(usuario: Usuario): void { this.usuarioEnDetalle = usuario; }

  protected cancelarFormulario(): void {
    this.formularioVisible = false;
    this.usuarioSeleccionado = null;
    this.error = '';
  }
}
