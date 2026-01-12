package com.svalero.cityEvents.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private int rate;
    @Column
    private String comment;
    @Column(name = "register_date")
    private LocalDate registerDate;
    @Column
    private boolean visible;
    @Column
    private boolean recommend;

    @ManyToOne  //establecemos la relaciones que tienen entre ellos
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
