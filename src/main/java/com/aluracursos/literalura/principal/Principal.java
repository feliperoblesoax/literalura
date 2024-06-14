package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConvierteDatos;
import com.aluracursos.literalura.service.ConsumoApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    @Autowired
    private LibroRepository libroRepositorio;
    @Autowired
    private AutorRepository autorRepositorio;


    public Principal(AutorRepository autorRepository, LibroRepository libroRepository) {
        this.libroRepositorio = libroRepository;
        this.autorRepositorio = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            try {
                var menu = """
                    Elija la opción a través de su número, por favor
                    1 - Buscar libro por título 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    """;
                System.out.println(menu);
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarAutorPorTituloDeLibro();
                        break;
                    case 2:
                        listarLibrosRegistrados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivosPorAnio();
                        break;
                    case 5:
                        listarLibrosPorIdioma();
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida");
                }
            } catch (InputMismatchException e) {
                opcion = -1;
                System.out.println("Entrada incorrecta...intenta nuevamente ");
                teclado.nextLine();
            }
        }
    }

    private void buscarAutorPorTituloDeLibro() {
        System.out.println("Introduce el título del libro que deseas buscar: ");
        String tituloLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "%20"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()) {
            System.out.println("Libro encontrado");
            DatosLibro libro = libroBuscado.get();
            DatosAutor datosAutor = libro.autor().get(0);
            //Buscar el autor por su nombre
            Autor autorEnBase = autorRepositorio.findByNombre(datosAutor.nombre());
            if (autorEnBase == null) {
                Autor autor = new Autor(datosAutor);
                autorRepositorio.save(autor);
                Libro libroParaBase = new Libro(libro);
                libroParaBase.setAutor(autor);
                libroRepositorio.save(libroParaBase);
                System.out.println(libroParaBase);
            } else {
                Libro libroEnBase = libroRepositorio.findByTitulo(libro.titulo());
                if (libroEnBase == null) {
                    Autor autor = new Autor(datosAutor);
                    Libro libroParaBase = new Libro(libro);
                    libroParaBase.setAutor(autorEnBase);
                    libroRepositorio.save(libroParaBase);
                    System.out.println(libroParaBase);
                }
                else {
                    System.out.println(libroEnBase);
                    System.out.println("\n El libro ya fue agregado a la base con anterioridad\n");
                }
            }
        } else {
            System.out.println("Libro no encontrado");
        }

    }

    private void listarLibrosRegistrados() {
        List<Libro> librosEnBase = libroRepositorio.findAll();
        StringBuilder sb = new StringBuilder();
        for (Libro libro : librosEnBase) {
            sb.append(libro.toString());
            sb.append("\n");
        }
        System.out.println(sb.toString().trim());
    }

    private void listarAutoresRegistrados() {
        List<Autor> autoresEnBase = autorRepositorio.findAll();
        System.out.println("------------Autores--------------");
        for (int i = 0; i < autoresEnBase.size(); i++) {
            System.out.println("Autor: " + autoresEnBase.get(i).getNombre());
            System.out.println("Año de nacimiento: " +autoresEnBase.get(i).getFechaDeNacimiento());
            System.out.println("Año de fallecimiento: " +autoresEnBase.get(i).getFechaDeFallecimiento());
            System.out.println("Libros: " +autoresEnBase.get(i).toStringSoloTitulos());
            System.out.println("--------------------------------");
        }
    }

    private void listarAutoresVivosPorAnio() {
        LocalDate fechaActual = LocalDate.now();
        int yearActual = fechaActual.getYear();
        System.out.println("Introduce el año a consultar el autor o autores vivos: ");
        int year = teclado.nextInt();
        teclado.nextLine();

        if (year > 0 && year <= yearActual) {
            List<Autor> autoresEnBase = autorRepositorio.findAll();
            if (autoresEnBase.isEmpty()) {
                System.out.println("No existen autores registrados");
            } else {
                int autoresEncontrados = 0;
                for (int i = 0; i < autoresEnBase.size(); i++) {
                    if (year >= autoresEnBase.get(i).getFechaDeNacimiento() && year <= autoresEnBase.get(i).getFechaDeFallecimiento()) {
                        System.out.println(autoresEnBase.get(i).getNombre());
                        System.out.println(autoresEnBase.get(i).getFechaDeNacimiento());
                        System.out.println(autoresEnBase.get(i).getFechaDeFallecimiento());
                        System.out.println(autoresEnBase.get(i).toStringSoloTitulos());
                        System.out.println("--------------------------------");
                        autoresEncontrados++;
                    }
                }
                if (autoresEncontrados == 0) System.out.println("No se encontraron autores de ese año");
            }
        } else {
            System.out.println("El año no es válido");
        }

    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Elije el idioma a buscar: 
                es - Español
                en - Inglés
                fr - Francés
                pt - Portugués""");
        var idioma = teclado.nextLine();
        if (idioma.equalsIgnoreCase("es")
                || idioma.equalsIgnoreCase("en")
                || idioma.equalsIgnoreCase("fr")
                || idioma.equalsIgnoreCase("pt")) {
            List<Libro> librosPorIdioma = libroRepositorio.findByIdiomas(idioma.toLowerCase());
            StringBuilder sb = new StringBuilder();
            for (Libro libro : librosPorIdioma) {
                sb.append(libro.toString());
                sb.append("\n");
            }
            System.out.println(sb.toString().trim());
            //System.out.println(librosPorIdioma);
        } else {
            System.out.println("Opción invalida");
        }
    }
}
