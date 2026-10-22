package com.ucompensar.veterinaria.model;

import jakarta.persistence.*;
import com.ucompensar.veterinaria.model.Cita;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
public class Mascota {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private String nombrePropietario;
    private String telefonoPropietario;
    @OneToMany
    private List<Cita> citas;

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

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getTelefonoPropietario() {
        return telefonoPropietario;
    }

    public void setTelefonoPropietario(String telefonoPropietario) {
        this.telefonoPropietario = telefonoPropietario;
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }

    public List<Cita> getCitas() {
        return citas;
    }
}
