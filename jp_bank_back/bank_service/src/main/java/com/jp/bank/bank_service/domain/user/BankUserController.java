package com.jp.bank.bank_service.domain.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jp.bank.bank_service.auth.TokenService;
import com.jp.bank.bank_service.domain.user.dto.BankUserMeDTO;
import com.jp.bank.bank_service.domain.user.dto.PasswordCheckDTO;

@RestController
@RequestMapping("/user")
public class BankUserController {

    private final BankUserService bankUserService;
    private final TokenService tokenService;

    public BankUserController(BankUserService bankUserService, TokenService tokenService) {
        this.bankUserService = bankUserService;
        this.tokenService = tokenService;
    }

    @GetMapping("/me")
    public ResponseEntity<BankUserMeDTO> me(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(bankUserService.getUserMe(authHeader));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestHeader("Authorization") String authHeader, @RequestBody PasswordCheckDTO data) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("invalid Authorization header");
        }

        String token = authHeader.replaceAll("Bearer ", "");
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId == null) {
            throw new RuntimeException("Invalid or expired token");
        }

        boolean match = bankUserService.verifyPassword(userId, data);
        return ResponseEntity.ok(Map.of("valid", match));
    }
}
