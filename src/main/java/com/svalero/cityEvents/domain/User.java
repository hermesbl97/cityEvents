package com.svalero.cityEvents.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "usuarios")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String username;
    @Column
    private String name;
    @Column
    private String surname;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column
    private boolean active;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<Review> reviews;
}
