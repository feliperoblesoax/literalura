package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name= "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String idiomas;
    private Double numeroDeDescargas;
    @ManyToOne
    @JoinColumn(name="autor_id")
    private Autor autor;

    public Libro() {}

    public Libro(DatosLibro datosLibro) {

        this.titulo = datosLibro.titulo();

        String idioma;
        idioma = datosLibro.idiomas().get(0);
        this.idiomas = idioma;

        this.numeroDeDescargas = datosLibro.numeroDeDescargas();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(String idiomas) {

        this.idiomas = idiomas;
    }

    public Double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Double numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    @Override
    public String toString() {
        return "------LIBRO------" +
                "\nTitulo= " + titulo +
                "\nAutor= " + autor +
                "\nIdiomas= " + idiomas +
                "\nNumero de descargas= " + numeroDeDescargas +
                "\n-----------------\n";
    }







}
