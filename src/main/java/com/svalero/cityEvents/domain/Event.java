package com.svalero.cityEvents.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String name;
    @Column
    private String description;
    @Column(name = "event_date")
//    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    @Column
    private String category;
    @Column
    private int capacity;
    @Column
    private float price;
    @Column
    private boolean availability=true;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE) //esto permite borrar en cascada los elementos en las reviews
    @JsonBackReference
    private List<Review> reviews;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToMany
    @JoinTable(name = "event_artists", joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id"))
    private List<Artist> artists;
}
