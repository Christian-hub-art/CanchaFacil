package com.example.demo.Controladores;

import com.example.demo.Entidades.Calificacion;
import com.example.demo.Servicios.CalificacionService;
import com.example.demo.Servicios.ReservaService;
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
@RequestMapping("/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;
    private final ReservaService reservaService;

    @Autowired
    public CalificacionController(CalificacionService calificacionService, ReservaService reservaService) {
        this.calificacionService = calificacionService;
        this.reservaService = reservaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("calificaciones", calificacionService.listar());
        model.addAttribute("titulo", "Calificaciones");
        return "calificaciones/lista";
    }

    /** GET /calificaciones?espacioId=1 -> calificaciones de un espacio. */
    @GetMapping(params = "espacioId")
    public String listarPorEspacio(@RequestParam Long espacioId, Model model) {
        model.addAttribute("calificaciones", calificacionService.listarPorEspacio(espacioId));
        model.addAttribute("titulo", "Calificaciones del espacio " + espacioId);
        return "calificaciones/lista";
    }

    @GetMapping("/add")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("calificacion", new Calificacion());
        model.addAttribute("reservas", reservaService.listar());
        model.addAttribute("accion", "Crear calificacion");
        return "calificaciones/formulario";
    }

    @GetMapping("/update/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Calificacion calificacion = calificacionService.buscarPorId(id);
        if (calificacion == null) {
            return "redirect:/calificaciones";
        }
        model.addAttribute("calificacion", calificacion);
        model.addAttribute("reservas", reservaService.listar());
        model.addAttribute("accion", "Editar calificacion");
        return "calificaciones/formulario";
    }

    @PostMapping("/add")
    public String guardar(@ModelAttribute("calificacion") Calificacion calificacion,
                          @RequestParam("reservaId") Long reservaId,
                          Model model) {
        try {
            calificacionService.guardar(calificacion, reservaId);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("reservas", reservaService.listar());
            model.addAttribute("accion", "Crear calificacion");
            return "calificaciones/formulario";
        }
        return "redirect:/calificaciones";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        calificacionService.eliminar(id);
        return "redirect:/calificaciones";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Calificacion calificacion = calificacionService.buscarPorId(id);
        if (calificacion == null) {
            return "redirect:/calificaciones";
        }
        model.addAttribute("calificacion", calificacion);
        return "calificaciones/detalle";
    }
}
