package com.radutodosan.users.repositories;

import com.radutodosan.users.entities.AppUser;
import com.radutodosan.users.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByOwner(AppUser user);
    Optional<Car> findByIdAndOwner(Long id, AppUser user);
}
