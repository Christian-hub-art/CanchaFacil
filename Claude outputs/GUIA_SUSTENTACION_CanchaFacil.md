# Guía para sustentar CanchaFacil

Esta guía explica, capa por capa, cómo está armado tu proyecto para que puedas defenderlo con tus propias palabras. Al final hay preguntas típicas de sustentación y una sección sobre cómo reaccionar si la profesora pide modificar o agregar algo en vivo.

---

## 1. Qué es CanchaFacil y cómo arranca

Es una aplicación web para reservar canchas deportivas, hecha con **Spring Boot** (el framework) + **Thymeleaf** (el motor que convierte tus HTML en páginas dinámicas) + **PostgreSQL** (la base de datos real, no en memoria).

**Punto de entrada:** `DemoApplication.java`

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

`@SpringBootApplication` es en realidad tres anotaciones en una:
- `@Configuration`: esta clase puede definir configuración.
- `@EnableAutoConfiguration`: Spring Boot mira qué dependencias tienes en el `pom.xml` (ve `spring-boot-starter-web`, por ejemplo) y configura solo un servidor Tomcat embebido, Thymeleaf, etc. Es la "magia" de Spring Boot: configuración automática según lo que declaraste.
- `@ComponentScan`: le dice a Spring "recorre este paquete y todos sus subpaquetes buscando clases anotadas con `@Controller`, `@Service`, `@Repository`, etc., y créalas como objetos que puedas usar (beans)".

**`application.properties`** — la configuración de la app:

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/canchafacil
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=true
```

Si te preguntan "¿de dónde saca la base de datos?": aquí. `ddl-auto=update` significa que **Hibernate (el traductor entre Java y SQL) crea y ajusta las tablas solo**, leyendo las entidades `@Entity`. No tuviste que escribir `CREATE TABLE` a mano.

`open-in-view=true` es importante y te lo pueden preguntar: mantiene la conexión a la base de datos abierta mientras Thymeleaf arma la página, para que puedas leer relaciones "perezosas" (`LAZY`, ver más abajo) directamente desde el HTML sin que explote con un error de sesión cerrada.

---

## 2. La arquitectura en capas (lo más importante para explicar)

El proyecto sigue el patrón **Modelo–Vista–Controlador** dividido en 4 capas, cada una con una sola responsabilidad:

```
Navegador
   │  GET /usuarios
   ▼
CONTROLADOR (@Controller)        recibe la petición, no tiene lógica de negocio
   │  usuarioService.listar()
   ▼
SERVICIO (@Service)              reglas de negocio: validar, decidir, calcular
   │  usuarioRepository.findAll()
   ▼
REPOSITORIO (@Repository / interfaz JpaRepository)   habla con la base de datos
   │
   ▼
PostgreSQL (tablas reales)
   │
   ▼  el controlador mete los datos en el Model
VISTA (Thymeleaf, templates/*.html)
   │
   ▼
HTML que ve el usuario
```

**Por qué se hace así (la pregunta que casi seguro te hacen):** cada capa solo conoce a la de al lado. El controlador no sabe cómo se guardan los datos, solo le pide al servicio "guárdame esto". El servicio no sabe si los datos van a PostgreSQL, a MySQL o a un archivo, solo usa el repositorio. Esto se llama **separación de responsabilidades**, y la ventaja es que puedes cambiar una capa (por ejemplo la base de datos) sin tocar las demás.

### Estructura de carpetas

```
src/main/java/com/example/demo/
├── DemoApplication.java
├── Entidades/        → las 7 clases que representan las tablas
├── Repositorios/      → interfaces que acceden a la base de datos
├── Servicios/          → reglas de negocio
└── Controladores/     → atienden al navegador (8 controladores)

src/main/resources/
├── application.properties
├── static/css/estilos.css     → el navegador lo pide directo: /css/estilos.css
└── templates/                  → Thymeleaf las procesa antes de enviarlas, nunca se piden directo
```

---

## 3. Capa de Entidades (el modelo de datos)

Hay **7 entidades + 1 enum**. Cada entidad es una clase Java que representa una tabla de la base de datos.

| Entidad | Representa | Se relaciona con |
|---|---|---|
| `Usuario` | Una persona (cliente o administrador) | Negocios, Reservas, Calificaciones, Notificaciones |
| `Negocio` | La empresa dueña de las canchas | Un `Usuario` administrador, muchos `Espacio` |
| `Espacio` | Una cancha concreta | Un `Negocio`, muchas `Reserva` y `Calificacion` |
| `Reserva` | Un usuario aparta un espacio en fecha/hora | `Usuario`, `Espacio`, un `Pago`, una `Calificacion` |
| `Pago` | El pago de una reserva | Una `Reserva` (1 a 1) |
| `Calificacion` | Puntaje de 1 a 5 tras usar la cancha | `Usuario`, `Espacio`, `Reserva` (1 a 1) |
| `Notificacion` | Un aviso para un usuario | Un `Usuario` |
| `Rol` | *enum*: `CLIENTE` o `ADMINISTRADOR` | — |

### Anotaciones de JPA/Hibernate (convierten la clase en tabla)

Toma como ejemplo `Usuario.java` y ve explicando línea por línea:

```java
@Entity                          // "esta clase es una tabla de la base de datos"
@Table(name = "usuarios")        // el nombre real de la tabla en PostgreSQL
public class Usuario {

    @Id                                              // esta columna es la llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // PostgreSQL genera el número solo (autoincremental / BIGSERIAL)
    private Long id;

    @Column(nullable = false, length = 120)   // NOT NULL, VARCHAR(120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 150)  // no se puede repetir el email
    private String email;

    @Enumerated(EnumType.STRING)   // guarda "CLIENTE" / "ADMINISTRADOR" como texto, no como 0/1
    private Rol rol;

    @OneToMany(mappedBy = "administrador", cascade = CascadeType.ALL)
    private List<Negocio> negocios = new ArrayList<>();
}
```

**Las relaciones — esto es lo que más preguntan:**

- `@ManyToOne`: "muchos de esta entidad pertenecen a uno de la otra". Ejemplo: muchos `Espacio` pertenecen a un `Negocio`. Es el lado **dueño** de la relación: ahí se crea la columna de llave foránea (`@JoinColumn(name = "negocio_id")`).
- `@OneToMany(mappedBy = "...")`: el lado inverso, "uno tiene muchos". `mappedBy` dice "la columna de la relación no vive aquí, vive en el campo `negocio` de la clase `Espacio`". No genera columna, solo te deja navegar la lista desde el lado "uno".
- `@OneToOne`: relación de uno a uno, como `Reserva` ↔ `Pago` (una reserva tiene máximo un pago). El `unique = true` en el `@JoinColumn` es lo que obliga a que sea 1 a 1 y no 1 a muchos.
- `fetch = FetchType.LAZY`: "no traigas este dato relacionado hasta que alguien lo pida explícitamente" (con `getAdministrador()`, por ejemplo). Es más eficiente que traer todo siempre. Por eso existe `open-in-view=true`: para que esa carga diferida siga funcionando mientras Thymeleaf arma el HTML.
- `cascade = CascadeType.ALL`: si borras el "uno", se borran en cadena los "muchos" relacionados (por ejemplo, si borras una `Reserva`, se borra su `Pago` y su `Calificacion` en vez de fallar por llave foránea).

### Anotaciones de Lombok (te ahorran código repetitivo)

```java
@Data                   // genera automáticamente getters, setters, toString, equals y hashCode
@NoArgsConstructor       // constructor vacío: new Usuario() — lo necesitan Thymeleaf y JPA
@AllArgsConstructor      // constructor con todos los campos
@ToString(exclude = {"negocios", "reservas", "calificaciones", "notificaciones"})
@EqualsAndHashCode(exclude = {"negocios", "reservas", "calificaciones", "notificaciones"})
```

**Pregunta trampa clásica:** *"¿por qué excluyen esas listas del `toString`/`equals`?"* Respuesta: porque las relaciones van en los dos sentidos (`Usuario` → `Negocio` → `administrador` → `Usuario` otra vez...). Si Lombok intentara imprimir o comparar todo el objeto completo, entraría en un bucle infinito (`StackOverflowError`). Por eso se excluye el lado que "cierra el círculo".

### Un detalle fino en `Reserva`

```java
@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
private LocalDate fecha;

@DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
private LocalTime horaInicio;
```

El formulario HTML manda la fecha como texto (`"2026-09-01"`, lo que produce un `<input type="date">`). Esta anotación le dice a Spring cómo convertir ese texto al tipo `LocalDate`/`LocalTime` de Java automáticamente al recibir el formulario.

---

## 4. Capa de Repositorios (acceso a datos)

Aquí es donde se nota que usas **Spring Data JPA**, no una capa de acceso a datos escrita a mano. Cada repositorio es solo una **interfaz**:

```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmailIgnoreCase(String email);
    List<Usuario> findByRol(Rol rol);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
}
```

**No hay ninguna clase que la implemente.** Spring Data JPA lee el nombre de la interfaz en tiempo de ejecución y **genera la implementación sola**, incluyendo el SQL. Esto es lo más importante que puedes explicar de esta capa:

- Al extender `JpaRepository<Usuario, Long>` ya vienen gratis: `findAll()`, `findById(id)`, `save(entidad)`, `deleteById(id)`, `count()`, `existsById(id)`.
- Los métodos que escribiste (`findByEmailIgnoreCase`, `findByRol`...) se llaman **"derived queries" (consultas derivadas)**: Spring **lee el nombre del método** y arma el SQL solo. `findByEmailIgnoreCase(String email)` se traduce a `SELECT * FROM usuarios WHERE LOWER(email) = LOWER(?)`. `findByNombreContainingIgnoreCase("jua")` encuentra "Juan" y "Juana" (`LIKE '%jua%'`).
- Cuando el nombre no alcanza (por ejemplo, filtrar notificaciones no leídas), se escribe la consulta a mano con `@Query` en JPQL (que es como SQL pero sobre entidades, no sobre tablas):

```java
@Query("select n from Notificacion n where n.usuario.id = :usuarioId and n.leido = false")
List<Notificacion> findByUsuarioIdYNoLeidas(@Param("usuarioId") Long usuarioId);
```

**Repositorios del proyecto y sus búsquedas propias:**

| Repositorio | Métodos propios |
|---|---|
| `UsuarioRepository` | `findByEmailIgnoreCase`, `findByRol`, `findByNombreContainingIgnoreCase` |
| `NegocioRepository` | `findByNitIgnoreCase`, `findByAdministradorId`, `findByNombreContainingIgnoreCase` |
| `EspacioRepository` | `findByNegocioId`, `findByTipoDeporteIgnoreCase` |
| `ReservaRepository` | `findByUsuarioId`, `findByEspacioId`, `findByEstadoIgnoreCase`, `findByEspacioIdAndFecha` |
| `PagoRepository` | `findByReservaId`, `findByReferenciaIgnoreCase`, `findByEstadoIgnoreCase` |
| `CalificacionRepository` | `findByEspacioId`, `findByUsuarioId`, `findByReservaId` |
| `NotificacionRepository` | `findByUsuarioId`, `findByUsuarioIdYNoLeidas` (con `@Query`) |

---

## 5. Capa de Servicios (las reglas de negocio)

Aquí se decide **si algo se puede hacer**, no solo "guardar lo que llegue". Patrón general:

```java
@Service
@Transactional(readOnly = true)     // por defecto, todo método es una transacción de solo lectura
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;   // nunca se crea con "new"

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {   // Spring lo inyecta solo
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional               // este método SÍ escribe, sobreescribe el readOnly de la clase
    public Usuario guardar(Usuario usuario) { ... }
}
```

- `@Service`: le dice a Spring "esta clase contiene lógica de negocio, créala como bean".
- **Inyección de dependencias por constructor:** el servicio no busca su repositorio, Spring se lo entrega. Esto se llama *Inversión de Control (IoC)*: en vez de que la clase controle sus dependencias, el framework las controla y las "inyecta".
- `@Transactional(readOnly = true)` a nivel de clase + `@Transactional` en los métodos que escriben: una transacción agrupa varias operaciones de base de datos como una sola unidad ("todo o nada"). Marcar de solo lectura las consultas es una optimización; los métodos que sí modifican datos (`guardar`, `eliminar`, `cambiarEstado`) llevan su propio `@Transactional` para poder escribir.

### Reglas de negocio por servicio (esto es lo que más te van a preguntar de "cómo funciona")

**`UsuarioService`**
- El email no se puede repetir (se busca por email antes de guardar).
- La `fechaRegistro` se asigna sola al crear y se conserva al editar.

**`NegocioService`**
- El NIT es único.
- Todo negocio debe tener un administrador (`Usuario`) que exista de verdad.

**`EspacioService`**
- El precio por hora no puede ser negativo.
- Todo espacio pertenece a un negocio existente.

**`ReservaService`** — la más interesante:
- La hora de inicio debe ser anterior a la hora de fin.
- **Un espacio no puede tener dos reservas activas que se crucen** el mismo día. Así se detecta el cruce de horarios:

```java
otra.getHoraInicio().isBefore(reserva.getHoraFin())
    && otra.getHoraFin().isAfter(reserva.getHoraInicio())
```
  Esto compara los rangos de horas: si el inicio de "otra" reserva es antes de que termine la nueva, **y** el fin de "otra" es después de que empiece la nueva, entonces se solapan. Las reservas en estado `CANCELADA` se ignoran, y al editar se ignora la propia reserva (para no chocar contra sí misma).
- Toda reserva nace en estado `PENDIENTE`.

**`PagoService`**
- El monto no puede ser negativo.
- Una reserva solo puede tener un pago (por el `unique = true` de la relación, respaldado también aquí en el servicio).
- Al **aprobar** un pago, la reserva pasa sola a `CONFIRMADA`. Al **rechazar** o **reembolsar**, la reserva pasa a `CANCELADA`. Esto es un efecto en cadena entre dos entidades distintas, útil de mencionar.

**`CalificacionService`**
- La puntuación debe estar entre 1 y 5.
- Cada reserva se puede calificar una sola vez.
- El usuario y el espacio de la calificación **se toman de la reserva**, no se piden en el formulario — así es imposible calificar una cancha en la que nunca reservaste.
- Calcula el promedio de un espacio con `stream().mapToInt(...).average()`.

**`NotificacionService`**
- Toda notificación nace no leída (`leido = false`) y con la fecha del momento.
- Se puede marcar como leída.

---

## 6. Capa de Controladores

Hay **8 controladores**: uno por entidad (`UsuarioController`, `NegocioController`, `EspacioController`, `ReservaController`, `PagoController`, `CalificacionController`, `NotificacionController`) más `HomeController` para la página de inicio.

Patrón que se repite en los 7 controladores de entidad:

```java
@Controller                        // "esta clase atiende peticiones HTTP y devuelve vistas"
@RequestMapping("/usuarios")       // todas las rutas de esta clase empiezan por /usuarios
public class UsuarioController {

    @GetMapping                     // GET /usuarios
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";     // el nombre del archivo en templates/, sin ".html"
    }

    @GetMapping("/{id}")            // GET /usuarios/5
    public String detalle(@PathVariable("id") Long id, Model model) { ... }

    @GetMapping("/add")             // muestra el formulario vacío
    public String mostrarFormularioCrear(Model model) { ... }

    @PostMapping("/add")            // recibe el formulario que se envió
    public String guardar(@ModelAttribute("usuario") Usuario usuario, Model model) { ... }

    @GetMapping("/update/{id}")     // formulario ya cargado con los datos existentes
    @GetMapping("/delete/{id}")     // elimina
    @GetMapping(params = "nombre")  // GET /usuarios?nombre=Ana → búsqueda
}
```

Anotaciones clave:
- `@Controller` (no `@RestController`): devuelve el **nombre de una vista** (String), no JSON. `@RestController` sería para una API.
- `@GetMapping` / `@PostMapping`: qué verbo HTTP y qué ruta atiende cada método.
- `@PathVariable`: toma un pedazo de la URL (`/usuarios/5` → `id = 5`).
- `@RequestParam`: toma un parámetro de query string (`?nombre=Ana` → `nombre = "Ana"`).
- `@ModelAttribute`: Spring arma automáticamente un objeto `Usuario` con los campos que llegaron del formulario (por nombre de campo).
- `Model`: una "caja" temporal donde el controlador deja datos para que la vista los lea. `model.addAttribute("usuarios", lista)` deja la lista con el nombre `"usuarios"`, y en el HTML se lee con `${usuarios}`.

### El patrón POST → Redirect → GET

Después de guardar, el controlador no devuelve un HTML directamente, devuelve una redirección:

```java
return "redirect:/usuarios";
```

Así, si el usuario recarga la página después de guardar, el navegador vuelve a pedir `/usuarios` (un `GET`) en vez de reenviar el mismo formulario (`POST`) y duplicar el registro.

### Manejo de errores de negocio

Cuando un servicio lanza `IllegalArgumentException` (por ejemplo, email duplicado), el controlador la captura y vuelve a mostrar el formulario con el mensaje, en vez de que la app se caiga con una página de error:

```java
try {
    usuarioService.guardar(usuario);
} catch (IllegalArgumentException ex) {
    model.addAttribute("error", ex.getMessage());
    return "usuarios/formulario";
}
return "redirect:/usuarios";
```

Y en el HTML: `<div class="error" th:if="${error}" th:text="${error}"></div>`.

### `HomeController`

Solo tiene una ruta, `GET /`, y arma los 7 contadores (`totalUsuarios`, `totalNegocios`, etc.) llamando a `.listar().size()` de cada servicio, para mostrarlos en el panel de inicio.

---

## 7. Mapa completo de URLs

| Entidad | Listar | Detalle | Crear | Editar | Eliminar | Filtros propios |
|---|---|---|---|---|---|---|
| Usuarios | `GET /usuarios` | `/usuarios/{id}` | `GET`/`POST /usuarios/add` | `/usuarios/update/{id}` | `/usuarios/delete/{id}` | `?nombre=` |
| Negocios | `GET /negocios` | `/negocios/{id}` | `/negocios/add` | `/negocios/update/{id}` | `/negocios/delete/{id}` | `?nombre=` |
| Espacios | `GET /espacios` | `/espacios/{id}` (incluye promedio de calificación) | `/espacios/add` | `/espacios/update/{id}` | `/espacios/delete/{id}` | `?deporte=` |
| Reservas | `GET /reservas` | `/reservas/{id}` | `/reservas/add` | `/reservas/update/{id}` | `/reservas/delete/{id}` | `?estado=`, `?usuarioId=`, `/reservas/cancelar/{id}` |
| Pagos | `GET /pagos` | `/pagos/{id}` | `/pagos/add` | `/pagos/update/{id}` | `/pagos/delete/{id}` | `?estado=`, `/pagos/estado/{id}?valor=` |
| Calificaciones | `GET /calificaciones` | `/calificaciones/{id}` | `/calificaciones/add` | `/calificaciones/update/{id}` | `/calificaciones/delete/{id}` | `?espacioId=` |
| Notificaciones | `GET /notificaciones` | `/notificaciones/{id}` | `/notificaciones/add` | `/notificaciones/update/{id}` | `/notificaciones/delete/{id}` | `?usuarioId=`, `/notificaciones/no-leidas/{usuarioId}`, `/notificaciones/leer/{id}` |
| Inicio | `GET /` | — | — | — | — | — |

---

## 8. Las vistas (Thymeleaf)

Cada entidad tiene 3 plantillas: `lista.html` (tabla o tarjetas + buscador + acciones), `formulario.html` (sirve para crear **y** editar: el `id` viaja oculto — si viene lleno, el servicio actualiza en vez de crear) y `detalle.html` (ficha completa).

**Fragmento reutilizable:** en vez de repetir el menú en 22 archivos, existe `templates/fragmentos/cabecera.html` con `<header th:fragment="cabecera">...</header>`, y cada página lo inserta con una sola línea: `<header th:replace="~{fragmentos/cabecera :: cabecera}"></header>`.

Sintaxis de Thymeleaf que vas a ver en el código:

| Expresión | Para qué sirve |
|---|---|
| `${...}` | Lee un dato del `Model` (lo que puso el controlador) |
| `*{...}` | Lee un campo del objeto del formulario (dentro de `th:object`) |
| `@{...}` | Construye una URL |
| `th:each` | Recorre una lista (`th:each="usuario : ${usuarios}"`) |
| `th:text` | Escribe texto dentro de la etiqueta |
| `th:if` / `th:unless` | Muestra u oculta según una condición |
| `th:field` | Conecta un `<input>` con un atributo del objeto (`th:field="*{nombre}"`) |
| `th:classappend` | Agrega una clase CSS extra según una condición |
| `th:replace` | Inserta un fragmento de otro archivo |

**Nota sobre el diseño:** el CSS (`static/css/estilos.css`) y el HTML fueron rediseñados para parecerse a un mockup de Figma (colores verdes, tarjetas redondeadas, cabecera con isotipo, cuadrícula de canchas). Esa parte es solo presentación (HTML/CSS), no cambia nada de la lógica explicada arriba.

---

## 9. Preguntas típicas de sustentación (con respuesta corta ya preparada)

**¿Por qué separaron el proyecto en Controlador / Servicio / Repositorio?**
> Para que cada capa tenga una sola responsabilidad y se puedan cambiar por separado. El controlador solo atiende HTTP, el servicio solo valida reglas de negocio, el repositorio solo habla con la base de datos.

**¿Cómo llegan los datos del formulario a la base de datos?**
> El HTML envía un `POST` con los campos del formulario → Spring arma un objeto `Usuario` con `@ModelAttribute` → el controlador se lo pasa al servicio → el servicio valida y llama al repositorio → el repositorio (JpaRepository) genera el `INSERT`/`UPDATE` en PostgreSQL a través de Hibernate.

**¿Qué es Hibernate / JPA?**
> JPA es la especificación (el "contrato") de Java para mapear clases a tablas. Hibernate es la implementación que realmente lo hace: traduce `usuarioRepository.save(usuario)` en sentencias SQL.

**¿Dónde se guardan los datos, se pierden al apagar la app?**
> No, se guardan en PostgreSQL (base de datos real), configurada en `application.properties`. No se pierden al reiniciar la aplicación.

**¿Qué pasa si dos personas reservan la misma cancha a la misma hora?**
> `ReservaService.guardar()` revisa con `hayCruce()` si ya existe otra reserva no cancelada del mismo espacio cuyo rango de horas se solape con la nueva, y si es así lanza una excepción que el controlador muestra como error en el formulario.

**¿Por qué usan `Long` para los ids y no `int`?**
> `Long` soporta números más grandes sin desbordarse y es el tipo estándar que usa `GenerationType.IDENTITY` en Spring/Hibernate para llaves autoincrementales.

**¿Qué hace `@Transactional`?**
> Agrupa varias operaciones sobre la base de datos como una sola unidad: si algo falla a mitad de camino, se deshace todo (rollback) en vez de dejar datos a medias.

**¿Por qué las contraseñas están en texto plano?**
> Es una simplificación válida para un proyecto de clase. En un sistema real se cifrarían con `BCryptPasswordEncoder` de Spring Security antes de guardarlas.

**¿Qué es Lombok y por qué no se ven los getters/setters en el código?**
> Lombok es un procesador de anotaciones: durante la compilación, `@Data` genera automáticamente los getters, setters, `toString`, `equals` y `hashCode` sin que tengas que escribirlos. Por eso no aparecen escritos en el archivo, pero sí existen quando el proyecto compila.

---

## 10. Si te piden modificar o agregar algo en vivo

Este es el escenario que más nervios da, así que aquí va una guía paso a paso genérica. Casi cualquier pedido de la profesora cae en uno de estos 4 patrones:

### A) "Agreguen un campo nuevo a tal entidad" (ej: `Usuario` con un campo `ciudad`)
1. **Entidad**: agrega el atributo con su `@Column` en `Entidades/Usuario.java`.
   ```java
   @Column(length = 80)
   private String ciudad;
   ```
   Como Lombok genera el getter/setter solo, no hay que escribir nada más ahí (y como `ddl-auto=update`, Hibernate agrega la columna sola al reiniciar).
2. **Formulario**: agrega el `<label>` + `<input th:field="*{ciudad}">` en `usuarios/formulario.html`.
3. **Detalle/lista** (opcional): agrega un `<dt>/<dd>` o una `<td>` con `th:text="${usuario.ciudad}"` donde quieras mostrarlo.
4. El servicio y el controlador normalmente **no hay que tocarlos** — ya reciben el objeto completo con `@ModelAttribute`/`th:object`.

### B) "Agreguen una nueva regla de validación" (ej: "el teléfono debe tener 10 dígitos")
Se hace en el **servicio**, nunca en el controlador ni en la entidad:
```java
if (usuario.getTelefono() != null && !usuario.getTelefono().matches("\\d{10}")) {
    throw new IllegalArgumentException("El telefono debe tener 10 digitos");
}
```
El controlador ya sabe capturar `IllegalArgumentException` y mostrar el error — no hay que tocarlo.

### C) "Agreguen un filtro/búsqueda nuevo" (ej: buscar espacios por precio máximo)
1. **Repositorio**: agrega el método derivado (Spring lo implementa solo):
   ```java
   List<Espacio> findByPrecioHoraLessThanEqual(BigDecimal precioMaximo);
   ```
2. **Servicio**: un método corto que lo llame.
3. **Controlador**: un `@GetMapping(params = "precioMaximo")` nuevo, igual al patrón que ya usan `?deporte=`, `?estado=`, etc.
4. **Vista**: un campo más en el formulario de búsqueda.

### D) "Agreguen una entidad nueva" (poco probable en el momento, pero por si acaso)
Repite las 4 capas: `Entidades/NuevaCosa.java` (`@Entity`), `Repositorios/NuevaCosaRepository.java` (`extends JpaRepository`), `Servicios/NuevaCosaService.java` (reglas de negocio), `Controladores/NuevaCosaController.java` (rutas), y 3 plantillas en `templates/nuevacosa/`. Es el mismo patrón copiado de cualquiera de las 7 entidades existentes — `Notificacion` es la más simple para usar de plantilla porque solo se relaciona con `Usuario`.

**Consejo general para el momento de la sustentación:** si te piden algo y no estás seguro de en qué capa va, la regla es: *¿es sobre cómo se ve? → vista. ¿Es una regla de "esto no se puede hacer si..."? → servicio. ¿Es una ruta/URL nueva? → controlador. ¿Es un dato nuevo que hay que guardar? → entidad.*

---

## 11. Glosario rápido

- **ORM (Object-Relational Mapping):** técnica para representar tablas de una base de datos como clases de Java. JPA/Hibernate son un ORM.
- **Bean:** un objeto que Spring crea y administra por ti (en vez de que tú hagas `new`).
- **Inyección de dependencias:** en vez de que una clase cree lo que necesita, se lo entrega Spring (por constructor, en este proyecto).
- **DTO:** no se usa en este proyecto — las entidades viajan directo entre capas. En proyectos más grandes se usarían clases intermedias para no exponer la entidad completa.
- **JPQL:** el lenguaje de consultas de JPA, parecido a SQL pero sobre entidades Java en vez de tablas (`select n from Notificacion n where ...`).
- **LAZY vs EAGER:** LAZY carga el dato relacionado solo cuando se pide explícitamente; EAGER lo trae siempre de una. Este proyecto usa LAZY en todas las relaciones.
- **Cascade:** qué le pasa a los datos relacionados cuando se borra o guarda el "padre" (por ejemplo, `CascadeType.ALL` borra en cadena).

---

## Cómo sigue esto

Cuando quieras, dime "hazme preguntas" o "evalúame" y te voy a ir preguntando sobre estas capas (qué hace tal anotación, qué pasa si..., cómo agregarías tal cosa) para que practiques explicarlo con tus palabras antes de la sustentación. También puedo simular que soy la profesora y pedirte una modificación pequeña en vivo para que la resuelvas mientras te guío.
