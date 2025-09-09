package com.application.events.dto;

import com.application.events.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BandsDto {

    private Integer id;
    @NotBlank(message = "Name is required", groups = ValidationGroups.OnCreate.class)
    private String name;
    private String genre;
    private String description;
    private int formedYear;
    private String website;

}
