package com.example.demo.Controladores;

import com.example.demo.Entidades.Rol;
import com.example.demo.Entidades.Usuario;
import com.example.demo.Servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de la entidad Usuario.
 * Recibe las peticiones del navegador, pide la logica al servicio y devuelve
 * el nombre de la plantilla Thymeleaf que se debe mostrar.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** GET /usuarios -> lista completa. */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("titulo", "Usuarios");
        return "usuarios/lista";
    }

    /** GET /usuarios?nombre=Juan -> busqueda por nombre. */
    @GetMapping(params = "nombre")
    public String buscarPorNombre(@RequestParam String nombre, Model model) {
        model.addAttribute("usuarios", usuarioService.buscarPorNombre(nombre));
        model.addAttribute("titulo", "Usuarios que coinciden con: " + nombre);
        return "usuarios/lista";
    }

    /** GET /usuarios/add -> formulario vacio para crear. */
    @GetMapping("/add")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("accion", "Crear usuario");
        return "usuarios/formulario";
    }

    /** GET /usuarios/update/5 -> formulario con los datos del usuario 5. */
    @GetMapping("/update/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("accion", "Editar usuario");
        return "usuarios/formulario";
    }

    /** POST /usuarios/add -> guarda el usuario que viene del formulario. */
    @PostMapping("/add")
    public String guardar(@ModelAttribute("usuario") Usuario usuario, Model model) {
        try {
            usuarioService.guardar(usuario);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("roles", Rol.values());
            model.addAttribute("accion", "Crear usuario");
            return "usuarios/formulario";
        }
        return "redirect:/usuarios";
    }

    /** GET /usuarios/delete/5 -> elimina el usuario 5. */
    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }

    /** GET /usuarios/5 -> detalle de un usuario. */
    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario);
        return "usuarios/detalle";
    }
}
