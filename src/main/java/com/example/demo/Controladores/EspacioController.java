package com.example.demo.Controladores;

import com.example.demo.Entidades.Espacio;
import com.example.demo.Servicios.CalificacionService;
import com.example.demo.Servicios.EspacioService;
import com.example.demo.Servicios.NegocioService;
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
@RequestMapping("/espacios")
public class EspacioController {

    private final EspacioService espacioService;
    private final NegocioService negocioService;
    private final CalificacionService calificacionService;

    @Autowired
    public EspacioController(EspacioService espacioService,
                             NegocioService negocioService,
                             CalificacionService calificacionService) {
        this.espacioService = espacioService;
        this.negocioService = negocioService;
        this.calificacionService = calificacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("espacios", espacioService.listar());
        model.addAttribute("titulo", "Espacios deportivos");
        return "espacios/lista";
    }

    /** GET /espacios?deporte=futbol -> filtra por tipo de deporte. */
    @GetMapping(params = "deporte")
    public String buscarPorDeporte(@RequestParam String deporte, Model model) {
        model.addAttribute("espacios", espacioService.buscarPorDeporte(deporte));
        model.addAttribute("titulo", "Espacios de " + deporte);
        return "espacios/lista";
    }

    @GetMapping("/add")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("espacio", new Espacio());
        model.addAttribute("negocios", negocioService.listar());
        model.addAttribute("accion", "Crear espacio");
        return "espacios/formulario";
    }

    @GetMapping("/update/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Espacio espacio = espacioService.buscarPorId(id);
        if (espacio == null) {
            return "redirect:/espacios";
        }
        model.addAttribute("espacio", espacio);
        model.addAttribute("negocios", negocioService.listar());
        model.addAttribute("accion", "Editar espacio");
        return "espacios/formulario";
    }

    @PostMapping("/add")
    public String guardar(@ModelAttribute("espacio") Espacio espacio,
                          @RequestParam("negocioId") Long negocioId,
                          Model model) {
        try {
            espacioService.guardar(espacio, negocioId);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("negocios", negocioService.listar());
            model.addAttribute("accion", "Crear espacio");
            return "espacios/formulario";
        }
        return "redirect:/espacios";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        espacioService.eliminar(id);
        return "redirect:/espacios";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Espacio espacio = espacioService.buscarPorId(id);
        if (espacio == null) {
            return "redirect:/espacios";
        }
        model.addAttribute("espacio", espacio);
        model.addAttribute("promedio", calificacionService.promedioPorEspacio(id));
        model.addAttribute("calificaciones", calificacionService.listarPorEspacio(id));
        return "espacios/detalle";
    }
}
