package com.example.demo.Controladores;

import com.example.demo.Entidades.Pago;
import com.example.demo.Servicios.PagoService;
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
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final ReservaService reservaService;

    @Autowired
    public PagoController(PagoService pagoService, ReservaService reservaService) {
        this.pagoService = pagoService;
        this.reservaService = reservaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pagos", pagoService.listar());
        model.addAttribute("titulo", "Pagos");
        return "pagos/lista";
    }

    /** GET /pagos?estado=APROBADO -> filtra por estado. */
    @GetMapping(params = "estado")
    public String listarPorEstado(@RequestParam String estado, Model model) {
        model.addAttribute("pagos", pagoService.listarPorEstado(estado));
        model.addAttribute("titulo", "Pagos en estado " + estado);
        return "pagos/lista";
    }

    @GetMapping("/add")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("pago", new Pago());
        model.addAttribute("reservas", reservaService.listar());
        model.addAttribute("accion", "Registrar pago");
        return "pagos/formulario";
    }

    @GetMapping("/update/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Pago pago = pagoService.buscarPorId(id);
        if (pago == null) {
            return "redirect:/pagos";
        }
        model.addAttribute("pago", pago);
        model.addAttribute("reservas", reservaService.listar());
        model.addAttribute("accion", "Editar pago");
        return "pagos/formulario";
    }

    @PostMapping("/add")
    public String guardar(@ModelAttribute("pago") Pago pago,
                          @RequestParam("reservaId") Long reservaId,
                          Model model) {
        try {
            pagoService.guardar(pago, reservaId);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("reservas", reservaService.listar());
            model.addAttribute("accion", "Registrar pago");
            return "pagos/formulario";
        }
        return "redirect:/pagos";
    }

    /** GET /pagos/estado/5?valor=APROBADO -> aprueba, rechaza o reembolsa el pago. */
    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable("id") Long id, @RequestParam("valor") String valor) {
        pagoService.cambiarEstado(id, valor);
        return "redirect:/pagos";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Long id) {
        pagoService.eliminar(id);
        return "redirect:/pagos";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Pago pago = pagoService.buscarPorId(id);
        if (pago == null) {
            return "redirect:/pagos";
        }
        model.addAttribute("pago", pago);
        return "pagos/detalle";
    }
}
