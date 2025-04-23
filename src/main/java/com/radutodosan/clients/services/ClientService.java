package com.radutodosan.clients.services;

import com.radutodosan.clients.entities.Client;
import com.radutodosan.clients.repositories.ClientRepository;
import com.radutodosan.clients.dtos.LoginRequestDTO;
import com.radutodosan.clients.dtos.SignupRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public void signup(SignupRequestDTO signUpRequest) {
        // Check if username or email already exists
        if (clientRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        if (clientRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        Client client = Client.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .pictureUrl("https://robohash.org/" + signUpRequest.getUsername() + ".png")
                .build();

        clientRepository.save(client);
    }

    public Client authenticate(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        return clientRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public Client getByUsername(String username) {
        return clientRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

}
