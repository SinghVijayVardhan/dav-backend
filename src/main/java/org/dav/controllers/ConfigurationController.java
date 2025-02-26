package org.dav.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.dav.entity.Configuration;
import org.dav.exception.InternalServerException;
import org.dav.modals.ConfigurationDto;
import org.dav.modals.LibraryBookConfig;
import org.dav.services.ConfigurationService;
import org.dav.utils.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @PostMapping("ui/carousel")
    public ResponseEntity<List<String>> saveCarouselImages(@RequestBody List<String> imageUrls) {
        try {
            List<String> images = configurationService.saveCarouselImages(imageUrls);
            return ResponseEntity.ok(images);
        }catch (Exception exception){
            throw new InternalServerException(exception.getMessage());
        }
    }

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @PostMapping("drive/service-account")
    public ResponseEntity<Void> saveServiceAccountConfig(@RequestBody String jsonBody){
        configurationService.saveServiceAccountInfo(jsonBody);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @GetMapping("drive/service-account")
    public ResponseEntity<Configuration> getServiceAccountConfig(){
        return ResponseEntity.ok(configurationService.getServiceAccountConfig());
    }

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @GetMapping("library-config")
    public ResponseEntity<LibraryBookConfig> getLibraryConfig(){
        LibraryBookConfig libraryBookConfig = configurationService.getLibraryConfiguration();
        return ResponseEntity.ok(libraryBookConfig);
    }

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @PostMapping("library-config")
    public ResponseEntity<LibraryBookConfig> saveLibraryConfig(@RequestBody LibraryBookConfig libraryBookConfig){
        configurationService.saveLibraryConfig(libraryBookConfig);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @PostMapping("ui-config")
    public ResponseEntity<ConfigurationDto> saveCustomConfiguration(@RequestBody ConfigurationDto configurationDto){
        Configuration configuration = configurationService.saveConfigByType(configurationDto.getKey(), configurationDto.getData());
        return ResponseEntity.status(HttpStatus.CREATED).body(configurationDto);
    }

    @GetMapping("ui/config")
    public ResponseEntity<ConfigurationDto> getCustomConfiguration(@RequestParam("key") String key){
        JsonNode jsonNode = configurationService.getConfigurationByType(key);
        ConfigurationDto configurationDto = new ConfigurationDto(key, jsonNode);
        return ResponseEntity.ok(configurationDto);
    }
}
