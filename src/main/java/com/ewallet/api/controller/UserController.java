package com.ewallet.api.controller;

import com.ewallet.api.dto.request.GetMeRequest;
import com.ewallet.api.dto.request.RegisterRequest;
import com.ewallet.api.dto.response.WalletResponse;
import com.ewallet.api.model.User;
import com.ewallet.api.security.AuthenticationContext;
import com.ewallet.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping(value = "/me/{username}")
    public ResponseEntity<Optional<User>> test(@PathVariable String username) {
        var content = userService.getUserByUsername(username);

        return ResponseEntity.ok(Optional.ofNullable(content));
    }
}
