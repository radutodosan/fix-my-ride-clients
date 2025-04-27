package com.radutodosan.clients.controllers;

import com.radutodosan.clients.dtos.ApiResponseDTO;
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
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Current password is incorrect", null);
            return ResponseEntity.badRequest().body(error);
        }

        client.setPassword(passwordEncoder.encode(request.getNewPassword()));
        clientRepository.save(client);

        ApiResponseDTO<String> success = new ApiResponseDTO<>(true, "Password changed successfully", null);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/change-email")
    public ResponseEntity<?> changeEmail(@RequestBody ChangeEmailRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Client not found"));

        if (!passwordEncoder.matches(request.getPassword(), client.getPassword())) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Password is incorrect", null);
            return ResponseEntity.badRequest().body(error);
        }

        if (clientRepository.existsByEmail(request.getNewEmail())) {
            ApiResponseDTO<?> error = new ApiResponseDTO<>(false, "Email already in use", null);
            return ResponseEntity.badRequest().body(error);
        }

        client.setEmail(request.getNewEmail());
        clientRepository.save(client);

        ApiResponseDTO<String> success = new ApiResponseDTO<>(true, "Email changed successfully", null);
        return ResponseEntity.ok(success);
    }

}
