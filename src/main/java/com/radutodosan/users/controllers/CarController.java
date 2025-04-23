package com.radutodosan.users.controllers;

import com.radutodosan.users.dtos.ApiResponseDTO;
import com.radutodosan.users.dtos.CarRequestDTO;
import com.radutodosan.users.entities.Car;
import com.radutodosan.users.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping("/add")
    public ResponseEntity<?> addCar(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestBody CarRequestDTO carRequestDTO) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Not authenticated", null));
        }

        String username = userDetails.getUsername();

        Car saved = carService.addCar(carRequestDTO, username);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Car added successfully", saved));
    }

    @GetMapping("/my-cars")
    public ResponseEntity<?> getMyCars(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Not authenticated", null));
        }

        String username = userDetails.getUsername();

        List<Car> cars = carService.getCarsForUser(username);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Cars retrieved", cars));
    }


    @DeleteMapping("/{carId}")
    public ResponseEntity<?> deleteCar(@AuthenticationPrincipal UserDetails userDetails,
                                       @PathVariable Long carId) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Not authenticated", null));
        }

        String username = userDetails.getUsername();
        try {
            carService.deleteCar(carId, username);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Car deleted successfully", null));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDTO<>(false, e.getMessage(), null));
        }
    }
}
