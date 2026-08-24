

@RequestMapping("/usuario")
@Controller

public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listar")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "usuario/listar";
    }

    @GetMapping("/crear")
    public String crearUsuarioForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/crear";
    }

    @PostMapping("/crear")
    public String crearUsuario(@ModelAttribute("usuario") Usuario usuario) {
        usuarioService.crearUsuario(usuario);
        return "redirect:/usuario/listar";
    }

}
