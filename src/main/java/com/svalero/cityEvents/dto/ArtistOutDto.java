package com.svalero.cityEvents.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistOutDto {

    private long id;
    private String name;
    private String surname;
    private int followers;
    private String type;
}
