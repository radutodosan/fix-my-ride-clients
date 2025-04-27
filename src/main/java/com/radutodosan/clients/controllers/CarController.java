package com.radutodosan.clients.controllers;

import com.radutodosan.clients.dtos.ApiResponseDTO;
import com.radutodosan.clients.dtos.CarRequestDTO;
import com.radutodosan.clients.entities.Car;
import com.radutodosan.clients.services.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/client/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping("/add")
    public ResponseEntity<?> addCar(@AuthenticationPrincipal UserDetails userDetails,
                                    @RequestBody CarRequestDTO carRequestDTO) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "Client not authenticated", null));
        }

        String username = userDetails.getUsername();

        Car savedCar = carService.addCar(carRequestDTO, username);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Car added successfully", savedCar));
    }

    @GetMapping("/my-cars")
    public ResponseEntity<?> getMyCars(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "User not authenticated", null));
        }

        String username = userDetails.getUsername();

        List<Car> cars = carService.getCarsForClient(username);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Cars retrieved", cars));
    }

    @PutMapping("/update-car/{carId}")
    public ResponseEntity<?> updateCar(@PathVariable Long carId,
                                       @RequestBody CarRequestDTO request,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDTO<>(false, "User not authenticated", null));
        }

        try {
            Car updatedCar = carService.updateCar(carId, userDetails.getUsername(), request);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "Car updated successfully", updatedCar));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN) // aici pui explicit 403!
                    .body(new ApiResponseDTO<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete-car/{carId}")
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
