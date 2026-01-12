package com.svalero.cityEvents.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   //con esto le decimos que es un valor autonumerico para que se generen los valores de id por sí mismos
    private long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String category;
    @Column
    private String adress;
    @Column(name = "register_date")
    private LocalDate registerDate;
    @Column
    private boolean accesible;

}
