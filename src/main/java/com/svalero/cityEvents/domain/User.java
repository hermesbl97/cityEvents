package com.svalero.cityEvents.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.print.attribute.standard.MediaSize;
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
    @NotNull(message = "Username is mandatory")
    private String username;
    @Column
    @NotNull(message = "Name is mandatory")
    private String name;
    @Column
    @NotNull(message = "The surname is mandatory")
    private String surname;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "telephone_number")
    @Min(value = 600000000, message = "The telephone must have this structure 6XX XXX XXX")
    @Max(value = 699999999, message = "The telephone must have this structure 6XX XXX XXX")
    private int telephoneNumber;
    @Column
    private boolean active;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<Review> reviews;
}
