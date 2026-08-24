package com.example.demo.Repositorios;

import com.example.demo.Entidades.Rol;
import com.example.demo.Entidades.Usuario;

import java.util.List;

public interface UsuarioRepository extends CrudRepository<Usuario> {

    Usuario findByEmail(String email);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByNombre(String nombre);
}
