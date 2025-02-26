package org.dav.controllers;

import org.dav.exception.InternalServerException;
import org.dav.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping("health-check")
    public ResponseEntity<String> healthCheck(){
        String status = "Application is up and running";
        return ResponseEntity.ok(status);
    }

    @GetMapping("ui/carousel")
    public ResponseEntity<List<String>> getCarouselImages() {
        try {
            List<String> images = configurationService.getCarouselImages();
            return ResponseEntity.ok(images);
        }catch (IOException exception){
            throw new InternalServerException(exception.getMessage());
        }
    }

    @PostMapping("ui/carousel")
    public ResponseEntity<List<String>> saveCarouselImages(@RequestBody List<String> imageUrls) {
        try {
            List<String> images = configurationService.saveCarouselImages(imageUrls);
            return ResponseEntity.ok(images);
        }catch (Exception exception){
            throw new InternalServerException(exception.getMessage());
        }
    }

    @PostMapping("test/email")
    public void sendEmail(){
        configurationService.sendEmail();
    }

}
