package com.example.demo.Controladores;

import com.example.demo.Entidades.Reserva;
import com.example.demo.Servicios.EspacioService;
import com.example.demo.Servicios.ReservaService;
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
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    private final EspacioService espacioService;

    @Autowired
    public ReservaController(ReservaService reservaService,
                             UsuarioService usuarioService,
                             EspacioService espacioService) {
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
        this.espacioService = espacioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reservas", reservaService.listar());
        model.addAttribute("titulo", "Reservas");
        return "reservas/lista";
    }

    /** GET /reservas?estado=CONFIRMADA -> filtra por estado. */
    @GetMapping(params = "estado")
    public String listarPorEstado(@RequestParam String estado, Model model) {
        model.addAttribute("reservas", reservaService.listarPorEstado(estado));
        model.addAttribute("titulo", "Reservas en estado " + estado);
        return "reservas/lista";
    }

    /** GET /reservas?usuarioId=1 -> reservas de un usuario. */
    @GetMapping(params = "usuarioId")
    public String listarPorUsuario(@RequestParam Long usuarioId, Model model) {
        model.addAttribute("reservas", reservaService.listarPorUsuario(usuarioId));
        model.addAttribute("titulo", "Reservas del usuario " + usuarioId);
        return "reservas/lista";
    }

    @GetMapping("/add")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("reserva", new Reserva());
        cargarListas(model);
        model.addAttribute("accion", "Crear reserva");
        return "reservas/formulario";
    }

    @GetMapping("/update/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Reserva reserva = reservaService.buscarPorId(id);
        if (reserva == null) {
            return "redirect:/reservas";
        }
        model.addAttribute("reserva", reserva);
        cargarListas(model);
        model.addAttribute("accion", "Editar reserva");
        return "reservas/formulario";
    }

    @PostMapping("/add")
    public String guardar(@ModelAttribute("reserva") Reserva reserva,
                          @RequestParam("usuarioId") Long usuarioId,
                          @RequestParam("espacioId") Long espacioId,
                          Model model) {
        try {
            reservaService.guardar(reserva, usuarioId, espacioId);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarListas(model);
            model.addAttribute("accion", "Crear reserva");
            return "reservas/formulario";
        }
        return "redirect:/reservas";
    }

    /** GET /reservas/cancelar/5 -> cambia el estado a CANCELADA. */
    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable("id") Long id) {
        reservaService.cancelar(id);
        return "redirect:/reservas";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        reservaService.eliminar(id);
        return "redirect:/reservas";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Reserva reserva = reservaService.buscarPorId(id);
        if (reserva == null) {
            return "redirect:/reservas";
        }
        model.addAttribute("reserva", reserva);
        return "reservas/detalle";
    }

    private void cargarListas(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("espacios", espacioService.listar());
    }
}
