# BibliotecaEscolarEFT

EFT de Desarrollo Orientado a Objetos II. Sistema de gestión de biblioteca escolar desarrollado en Java, con interfaz gráfica Swing y persistencia en MySQL mediante JDBC.

## Funcionalidades

- Inicio de sesión con roles de bibliotecario y estudiante.
- Gestión de libros, estudiantes y categorías (CRUD).
- Registro de préstamos y devoluciones, con actualización automática del stock.
- Cálculo de vencimientos y detección de préstamos atrasados.
- Reportes de libros más prestados, historial por estudiante y préstamos vigentes.
- Acceso de estudiantes a consultas, préstamos y devoluciones de sus propios registros.

## Conceptos aplicados

- Programación orientada a objetos: abstracción, encapsulamiento, herencia y polimorfismo.
- Interfaces y validación de datos mediante `Validable`.
- Arquitectura MVC y patrón DAO con interfaces e implementaciones JDBC.
- Patrón Singleton para gestionar el acceso a conexiones.
- Programación concurrente con `SwingWorker` y sincronización con `synchronized`.
- Transacciones JDBC para mantener la consistencia entre préstamos y stock.
- Manejo de excepciones y mensajes de error en la interfaz.

## Configuración de la base de datos

1. En MySQL, ejecutar los archivos de la carpeta `sql/` **en este orden**: primero el script de creación de tablas y después el de poblado. Hacerlo sobre una base nueva; no volver a ejecutarlos sobre una base ya poblada.
2. Copiar `.env.example` con el nombre `.env`, en la raíz del proyecto.
3. Completar `DB_PASSWORD` con la contraseña local de MySQL y ajustar los demás valores si es necesario:

```properties
DB_URL=jdbc:mysql://localhost:3306/biblioteca
DB_USER=root
DB_PASSWORD=TU_CONTRASEÑA
```


```text
BibliotecaEscolarEFT/
│
├── src/
│   └── main/
│       ├── java/
│       │   ├── config/
│       │   │   ├── DatabaseConnection.java
│       │   │   └── Sesion.java
│       │   │
│       │   ├── controlador/
│       │   │   ├── ControladorAutenticacion.java
│       │   │   ├── ControladorCategorias.java
│       │   │   ├── ControladorEstudiantes.java
│       │   │   ├── ControladorLibros.java
│       │   │   ├── ControladorPrestamos.java
│       │   │   └── ControladorReportes.java
│       │   │
│       │   ├── dao/
│       │   │   ├── CategoriaDAO.java
│       │   │   ├── EstudianteDAO.java
│       │   │   ├── LibroDAO.java
│       │   │   ├── PrestamoDAO.java
│       │   │   ├── ReporteDAO.java
│       │   │   ├── UsuarioDAO.java
│       │   │   └── impl/
│       │   │       ├── CategoriaDAOImpl.java
│       │   │       ├── EstudianteDAOImpl.java
│       │   │       ├── LibroDAOImpl.java
│       │   │       ├── PrestamoDAOImpl.java
│       │   │       ├── ReporteDAOImpl.java
│       │   │       └── UsuarioDAOImpl.java
│       │   │
│       │   ├── modelo/
│       │   │   ├── Usuario.java
│       │   │   ├── Bibliotecario.java
│       │   │   ├── Estudiante.java
│       │   │   ├── Libro.java
│       │   │   ├── Categoria.java
│       │   │   ├── Prestamo.java
│       │   │   ├── PrestamoDetalle.java
│       │   │   ├── ReporteLibro.java
│       │   │   ├── ReporteHistorial.java
│       │   │   ├── ReportePrestamoActivo.java
│       │   │   └── Validable.java
│       │   │
│       │   ├── vista/
│       │   │   ├── Login.java
│       │   │   ├── VentanaPrincipal.java
│       │   │   ├── PanelLibros.java
│       │   │   ├── PanelEstudiantes.java
│       │   │   ├── PanelCategorias.java
│       │   │   ├── PanelPrestamos.java
│       │   │   ├── PanelDevoluciones.java
│       │   │   ├── PanelReportes.java
│       │   │   ├── RegistroLibro.java
│       │   │   ├── RegistroEstudiante.java
│       │   │   ├── EstiloTablas.java
│       │   │   └── GestorErrores.java
│       │   │
│       │   └── Main.java
│       │
│       └── resources/
│
├── sql/
│   ├── PRY2203_EFT_S9_Script_crea_tablas_biblioteca.sql
│   └── PRY2203_EFT_S9_Script_poblado_tablas_biblioteca.sql
│
├── .env.example
├── .gitignore
├── pom.xml
└── README.md
```


