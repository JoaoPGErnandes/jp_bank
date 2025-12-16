package com.jp.bank.bank_service.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.bank.bank_service.auth.dto.AuthLoginDTO;
import com.jp.bank.bank_service.auth.dto.AuthRegisterDTO;
import com.jp.bank.bank_service.auth.dto.LoginResponseDTO;
import com.jp.bank.bank_service.domain.user.BankUser;
import com.jp.bank.bank_service.domain.user.BankUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final BankUserService bankUserService;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, BankUserService bankUserService, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.bankUserService = bankUserService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((BankUser) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRegisterDTO data) {
        this.bankUserService.create(data);
        return ResponseEntity.ok().build();
    }

}
