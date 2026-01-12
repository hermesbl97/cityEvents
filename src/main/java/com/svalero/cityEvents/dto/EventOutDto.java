package com.svalero.cityEvents.dto;

import com.svalero.cityEvents.domain.Review;
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
public class EventOutDto {

    private long id;
    private String name;
    private LocalDate eventDate;
    private String category;
    private float price;
}
