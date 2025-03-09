package org.dav.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.dav.entity.Configuration;
import org.dav.modals.ConfigurationDto;
import org.dav.modals.LibraryBookConfig;
import org.dav.services.ConfigurationService;
import org.dav.utils.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


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

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @GetMapping("config")
    public ResponseEntity<Map<String,JsonNode>> getAllConfigurations(){
        Map<String, JsonNode> configurationList = configurationService.getAllConfigurations();
        return ResponseEntity.ok(configurationList);
    }
}
