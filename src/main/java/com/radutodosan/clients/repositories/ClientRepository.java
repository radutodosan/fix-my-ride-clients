package com.radutodosan.clients.repositories;

import com.radutodosan.clients.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUsername(String userName);
    boolean existsByUsername(String userName);
    boolean existsByEmail(String email);
}
