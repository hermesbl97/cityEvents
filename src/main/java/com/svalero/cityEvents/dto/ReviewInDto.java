package com.svalero.cityEvents.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewInDto { //los datos facilitados al registrar una review

    private LocalDate registerDate;
    private boolean recommend;
    private int rate;
    private String comment;
    private long eventId;
    private long userId;
}
