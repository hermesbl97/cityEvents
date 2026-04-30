package com.svalero.cityEvents.dto;

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
public class EventModifyInDto {
    @NotNull(message = "Name is mandatory")
    private String name;
    private String description;
    private LocalDate eventDate;
    @NotNull(message = "Category is mandatory")
    private String category;
    @Min(value = 1, message = "The capacity must be at least 1 person")
    private int capacity;
    @Min(value = 0, message = "The price must be a positive number")
    private float price;
    private boolean availability;
    private long locationId;

    private List<Long> artistsIds;
}
