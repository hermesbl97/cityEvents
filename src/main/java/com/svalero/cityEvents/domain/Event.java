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
@Entity(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @NotNull(message = "Name is mandatory")
    private String name;
    @Column
    private String description;
    @Column(name = "event_name")
    private LocalDate eventDate;
    @Column
    @NotNull(message = "Category is mandatory")
    private String category;
    @Column
    @Min(value = 1, message = "The capacity must be at least 1 person")
    private int capacity;
    @Column
    @Min(value = 0, message = "The price must be a positive number")
    private float price;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE) //esto permite borrar en cascada los elementos en las reviews
    @JsonBackReference
    private List<Review> reviews;
}
