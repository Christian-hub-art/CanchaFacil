package com.example.demo.Controladores;

import com.example.demo.Entidades.Notificacion;
import com.example.demo.Servicios.NotificacionService;
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

@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioService usuarioService;

    @Autowired
    public NotificacionController(NotificacionService notificacionService, UsuarioService usuarioService) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("notificaciones", notificacionService.listar());
        model.addAttribute("titulo", "Notificaciones");
        return "notificaciones/lista";
    }

    /** GET /notificaciones?usuarioId=1 -> notificaciones de un usuario. */
    @GetMapping(params = "usuarioId")
    public String listarPorUsuario(@RequestParam Long usuarioId, Model model) {
        model.addAttribute("notificaciones", notificacionService.listarPorUsuario(usuarioId));
        model.addAttribute("titulo", "Notificaciones del usuario " + usuarioId);
        return "notificaciones/lista";
    }

    /** GET /notificaciones/no-leidas/1 -> solo las pendientes de leer. */
    @GetMapping("/no-leidas/{usuarioId}")
    public String listarNoLeidas(@PathVariable("usuarioId") Long usuarioId, Model model) {
        model.addAttribute("notificaciones", notificacionService.listarNoLeidas(usuarioId));
        model.addAttribute("titulo", "Notificaciones sin leer del usuario " + usuarioId);
        return "notificaciones/lista";
    }

    @GetMapping("/add")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("notificacion", new Notificacion());
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("accion", "Crear notificacion");
        return "notificaciones/formulario";
    }

    @GetMapping("/update/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Notificacion notificacion = notificacionService.buscarPorId(id);
        if (notificacion == null) {
            return "redirect:/notificaciones";
        }
        model.addAttribute("notificacion", notificacion);
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("accion", "Editar notificacion");
        return "notificaciones/formulario";
    }

    @PostMapping("/add")
    public String guardar(@ModelAttribute("notificacion") Notificacion notificacion,
                          @RequestParam("usuarioId") Long usuarioId,
                          Model model) {
        try {
            notificacionService.guardar(notificacion, usuarioId);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("usuarios", usuarioService.listar());
            model.addAttribute("accion", "Crear notificacion");
            return "notificaciones/formulario";
        }
        return "redirect:/notificaciones";
    }

    /** GET /notificaciones/leer/5 -> marca la notificacion como leida. */
    @GetMapping("/leer/{id}")
    public String marcarComoLeida(@PathVariable("id") Long id) {
        notificacionService.marcarComoLeida(id);
        return "redirect:/notificaciones";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        notificacionService.eliminar(id);
        return "redirect:/notificaciones";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Notificacion notificacion = notificacionService.buscarPorId(id);
        if (notificacion == null) {
            return "redirect:/notificaciones";
        }
        model.addAttribute("notificacion", notificacion);
        return "notificaciones/detalle";
    }
}
