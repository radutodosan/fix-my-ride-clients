package com.radutodosan.clients.controllers;

import com.radutodosan.clients.dtos.ChangeEmailRequestDTO;
import com.radutodosan.clients.dtos.ChangePasswordRequestDTO;
import com.radutodosan.clients.entities.Client;
import com.radutodosan.clients.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("clients/details")
@RequiredArgsConstructor
public class ChangeInformationController {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Client not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), client.getPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }

        client.setPassword(passwordEncoder.encode(request.getNewPassword()));
        clientRepository.save(client);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/change-email")
    public ResponseEntity<?> changeEmail(@RequestBody ChangeEmailRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Client not found"));

        // check if password matches
        if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
            return ResponseEntity.badRequest().body("Password is incorrect");
        }

        // check if email is already used
        if (clientRepository.existsByEmail(request.getNewEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        client.setEmail(request.getNewEmail());
        clientRepository.save(client);
        return ResponseEntity.ok("Email changed successfully");
    }

}
