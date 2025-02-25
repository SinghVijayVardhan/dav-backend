package org.dav.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/")
public class ConfigurationController {

    @GetMapping("health-check")
    public ResponseEntity<String> healthCheck(){
        String status = "Application is up and running";
        return ResponseEntity.ok(status);
    }
}
