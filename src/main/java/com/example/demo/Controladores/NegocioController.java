package com.example.demo.Controladores;

import com.example.demo.Entidades.Negocio;
import com.example.demo.Servicios.NegocioService;
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
@RequestMapping("/negocios")
public class NegocioController {

    private final NegocioService negocioService;
    private final UsuarioService usuarioService;

    @Autowired
    public NegocioController(NegocioService negocioService, UsuarioService usuarioService) {
        this.negocioService = negocioService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("negocios", negocioService.listar());
        model.addAttribute("titulo", "Negocios");
        return "negocios/lista";
    }

    @GetMapping(params = "nombre")
    public String buscarPorNombre(@RequestParam String nombre, Model model) {
        model.addAttribute("negocios", negocioService.buscarPorNombre(nombre));
        model.addAttribute("titulo", "Negocios que coinciden con: " + nombre);
        return "negocios/lista";
    }

    @GetMapping("/add")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("negocio", new Negocio());
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("accion", "Crear negocio");
        return "negocios/formulario";
    }

    @GetMapping("/update/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Negocio negocio = negocioService.buscarPorId(id);
        if (negocio == null) {
            return "redirect:/negocios";
        }
        model.addAttribute("negocio", negocio);
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("accion", "Editar negocio");
        return "negocios/formulario";
    }

    @PostMapping("/add")
    public String guardar(@ModelAttribute("negocio") Negocio negocio,
                          @RequestParam("administradorId") Long administradorId,
                          Model model) {
        try {
            negocioService.guardar(negocio, administradorId);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("usuarios", usuarioService.listar());
            model.addAttribute("accion", "Crear negocio");
            return "negocios/formulario";
        }
        return "redirect:/negocios";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        negocioService.eliminar(id);
        return "redirect:/negocios";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Negocio negocio = negocioService.buscarPorId(id);
        if (negocio == null) {
            return "redirect:/negocios";
        }
        model.addAttribute("negocio", negocio);
        return "negocios/detalle";
    }
}
