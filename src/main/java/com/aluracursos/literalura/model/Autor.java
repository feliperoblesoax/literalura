package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name="autores")

public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private int fechaDeNacimiento;
    private int fechaDeFallecimiento;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libro> libros;

    public Autor() {}

    public Autor(DatosAutor d) {
        this.nombre = d.nombre();
        try{
            this.fechaDeNacimiento = Optional.of(Integer.valueOf(d.fechaDeNacimiento())).orElse(0);
        } catch (DateTimeParseException e){
            this.fechaDeNacimiento = 0;
        }
        try{
            this.fechaDeFallecimiento = Optional.of(Integer.valueOf(d.fechaDeFallecimiento())).orElse(0);
        } catch (DateTimeParseException e){
            this.fechaDeFallecimiento = 0;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFechaDeNacimiento() {
        return fechaDeNacimiento;
    }

    public void setFechaDeNacimiento(int fechaDeNacimiento) {
        this.fechaDeNacimiento = fechaDeNacimiento;
    }

    public int getFechaDeFallecimiento() {
        return fechaDeFallecimiento;
    }

    public void setFechaDeFallecimiento(int fechaDeFallecimiento) {
        this.fechaDeFallecimiento = fechaDeFallecimiento;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        libros.forEach(l -> l.setAutor(this));
        this.libros = libros;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public String toStringCompleto() {
        return "Autor" +
                ", nombre='" + nombre + '\'' +
                ", fechaDeNacimiento=" + fechaDeNacimiento +
                ", fechaDeFallecimiento=" + fechaDeFallecimiento +
                ", libros=" + libros + "\n";
    }

    public String toStringSoloTitulos() {
        return this.libros.stream()
                .map(Libro::getTitulo)
                .collect(Collectors.joining(", "));
    }
}
