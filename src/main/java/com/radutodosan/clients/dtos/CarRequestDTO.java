package com.radutodosan.clients.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class CarRequestDTO {
    private String brand;
    private String model;
    private int year;
    private String licensePlate;
}
