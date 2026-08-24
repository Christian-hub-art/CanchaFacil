package com.example.demo.Controladores;

import com.example.demo.Servicios.CalificacionService;
import com.example.demo.Servicios.EspacioService;
import com.example.demo.Servicios.NegocioService;
import com.example.demo.Servicios.NotificacionService;
import com.example.demo.Servicios.PagoService;
import com.example.demo.Servicios.ReservaService;
import com.example.demo.Servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Pagina de inicio con el resumen de la aplicacion.
 */
@Controller
public class HomeController {

    private final UsuarioService usuarioService;
    private final NegocioService negocioService;
    private final EspacioService espacioService;
    private final ReservaService reservaService;
    private final PagoService pagoService;
    private final CalificacionService calificacionService;
    private final NotificacionService notificacionService;

    @Autowired
    public HomeController(UsuarioService usuarioService,
                          NegocioService negocioService,
                          EspacioService espacioService,
                          ReservaService reservaService,
                          PagoService pagoService,
                          CalificacionService calificacionService,
                          NotificacionService notificacionService) {
        this.usuarioService = usuarioService;
        this.negocioService = negocioService;
        this.espacioService = espacioService;
        this.reservaService = reservaService;
        this.pagoService = pagoService;
        this.calificacionService = calificacionService;
        this.notificacionService = notificacionService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalUsuarios", usuarioService.listar().size());
        model.addAttribute("totalNegocios", negocioService.listar().size());
        model.addAttribute("totalEspacios", espacioService.listar().size());
        model.addAttribute("totalReservas", reservaService.listar().size());
        model.addAttribute("totalPagos", pagoService.listar().size());
        model.addAttribute("totalCalificaciones", calificacionService.listar().size());
        model.addAttribute("totalNotificaciones", notificacionService.listar().size());
        return "index";
    }
}
