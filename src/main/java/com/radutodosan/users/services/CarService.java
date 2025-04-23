package com.radutodosan.users.services;

import com.radutodosan.users.dtos.CarRequestDTO;
import com.radutodosan.users.entities.AppUser;
import com.radutodosan.users.entities.Car;
import com.radutodosan.users.repositories.AppUserRepository;
import com.radutodosan.users.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final AppUserRepository appUserRepository;

    public Car addCar(CarRequestDTO dto, String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Car car = new Car(null, dto.getBrand(), dto.getModel(), dto.getYear(), dto.getLicensePlate(), user);
        return carRepository.save(car);
    }

    public List<Car> getCarsForUser(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return carRepository.findByOwner(user);
    }

    public void deleteCar(Long carId, String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Car car = carRepository.findByIdAndOwner(carId, user)
                .orElseThrow(() -> new NoSuchElementException("Car not found or not owned by user"));

        carRepository.delete(car);
    }
}
