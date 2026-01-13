package com.svalero.cityEvents.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "artists")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @NotNull(message = "Name is mandatory")
    private String name;
    @Column
    @NotNull(message = "Surname is mandatory")
    private String surname;
    @Column
    @NotNull(message = "Genre is mandatory")
    private String genre;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column
    @NotNull(message = "Type is mandatory")
    private String type;
    @Column
    @Min(value = 0, message = "This feature must be a positive number")
    private int followers;
    @Column
    private float height;
    @Column
    private boolean active;

    @ManyToMany(mappedBy = "artists")
    @JsonBackReference
    private List<Event> events;
}
