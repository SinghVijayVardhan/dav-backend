package org.dav.controllers;

import org.dav.modals.UserClaim;
import org.dav.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserClaim> login(@RequestParam("idToken") String idToken){
        UserClaim userClaim ;
        try{
            userClaim = userService.login(idToken);
        }catch(Exception exception){
            throw new RuntimeException(exception.getCause());
        }
        return ResponseEntity.ok(userClaim);
    }
}
