package org.dav.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dav.entity.Book;
import org.dav.entity.Configuration;
import org.dav.entity.Loan;
import org.dav.entity.User;
import org.dav.exception.InternalServerException;
import org.dav.exception.NotFoundException;
import org.dav.modals.LibraryBookConfig;
import org.dav.repository.ConfigurationRepository;
import org.dav.utils.ConfigurationKey;
import org.dav.utils.SmtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Service
public class ConfigurationService {

    private final SmtpService smtpService;
    private final ObjectMapper objectMapper;
    private final ConfigurationRepository configurationRepository;

    @Autowired
    public ConfigurationService(SmtpService smtpService, ObjectMapper objectMapper, ConfigurationRepository configurationRepository) {
        this.smtpService = smtpService;
        this.objectMapper = objectMapper;
        this.configurationRepository = configurationRepository;
    }

    public JsonNode getConfigurationByType(String type){
        Configuration configuration = configurationRepository.findConfigurationByType(type);
        if(configuration==null){
            throw new InternalServerException("Configuration missing for "+type);
        }
        return configuration.getData();
    }

    public void saveServiceAccountInfo(String jsonBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonBody);
            Configuration configuration = configurationRepository.findConfigurationByType(ConfigurationKey.SERVICE_ACCOUNT_CONFIG);
            configuration.setData(jsonNode);
            configurationRepository.save(configuration);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Incorrect json body");
        }
    }

    public Configuration getServiceAccountConfig() {
        return configurationRepository.findConfigurationByType(ConfigurationKey.SERVICE_ACCOUNT_CONFIG);
    }

    public LibraryBookConfig getLibraryConfiguration() {
        JsonNode jsonNode = getConfigurationByType(ConfigurationKey.LIBRARY_ISSUE_CONFIG);
        try {
            return objectMapper.readValue(jsonNode.traverse(), new TypeReference<LibraryBookConfig>(){});
        } catch (IOException e) {
            throw new RuntimeException("There is some error in configuration");
        }
    }

    public void saveLibraryConfig(LibraryBookConfig libraryBookConfig) {
        try {
            JsonNode jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(libraryBookConfig));
            Configuration configuration = configurationRepository.findConfigurationByType(ConfigurationKey.LIBRARY_ISSUE_CONFIG);
            configuration.setData(jsonNode);
            configurationRepository.save(configuration);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occured while saving data. Try again!!");
        }
    }

    public Configuration saveConfigByType(String key, JsonNode jsonNode){
        Configuration configuration = configurationRepository.findConfigurationByType(key);
        if(configuration==null){
            configuration = Configuration.builder().type(key).data(jsonNode).build();
        }else{
            configuration.setData(jsonNode);
        }
        return configurationRepository.save(configuration);
    }

    public Map<String, JsonNode> getAllConfigurations() {
        List<Configuration> configurations = configurationRepository.findAll();
        Map<String, JsonNode> configurationMap =  new HashMap<>();
        for(Configuration configuration : configurations){
            configurationMap.put(configuration.getType(), configuration.getData());
        }
        return configurationMap;
    }
}
