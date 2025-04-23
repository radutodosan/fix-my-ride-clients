package com.radutodosan.clients.repositories;

import com.radutodosan.clients.entities.Client;
import com.radutodosan.clients.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByOwner(Client user);
    Optional<Car> findByIdAndOwner(Long id, Client user);
}
