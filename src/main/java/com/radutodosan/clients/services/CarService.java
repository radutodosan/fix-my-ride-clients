package com.radutodosan.clients.services;

import com.radutodosan.clients.dtos.CarRequestDTO;
import com.radutodosan.clients.entities.Client;
import com.radutodosan.clients.entities.Car;
import com.radutodosan.clients.repositories.ClientRepository;
import com.radutodosan.clients.repositories.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final ClientRepository clientRepository;

    public Car addCar(CarRequestDTO carRequest, String username) {
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));

        Car car = new Car(null, carRequest.getBrand(), carRequest.getModel(), carRequest.getYear(), carRequest.getLicensePlate(), client);
        return carRepository.save(car);
    }

    public List<Car> getCarsForClient(String username) {
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));

        return carRepository.findByOwner(client);
    }

    public Car updateCar(Long carId, String username, CarRequestDTO request) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        if (!car.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("You can only update your own cars");
        }

        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setLicensePlate(request.getLicensePlate());

        return carRepository.save(car);
    }

    public void deleteCar(Long carId, String username) {
        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Client not found"));

        Car car = carRepository.findByIdAndOwner(carId, client)
                .orElseThrow(() -> new NoSuchElementException("Car not found or not owned by client"));

        carRepository.delete(car);
    }
}
