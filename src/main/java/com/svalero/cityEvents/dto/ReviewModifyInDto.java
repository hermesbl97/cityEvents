package com.svalero.cityEvents.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewModifyInDto { //los datos facilitados al registrar una review

    private LocalDate registerDate;
    private boolean recommend;
    @Min(value = 1, message = "The rate must be at least 1")
    @Max(value = 5, message = "The rate can not be over 5 ")
    private float rate;
    @NotBlank(message = "The comment can not be empty")
    private String comment;
    private int likes;
    private boolean visible;
    private long eventId;
    private long userId;
}
