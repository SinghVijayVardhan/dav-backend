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


    public List<String> getCarouselImages() throws IOException {
        Configuration configuration = configurationRepository.findConfigurationByType(ConfigurationKey.CAROUSEL_KEY);
        if(configuration==null || configuration.getData()==null){
            throw new NotFoundException("Carousel images are not configured");
        }
        TreeMap<Integer, String> images = objectMapper.readValue(configuration.getData().traverse(), new TypeReference<TreeMap<Integer,String>>(){});
        return images.values().stream().toList();
    }

    public List<String> saveCarouselImages(List<String> imageUrls) {
        Map<Integer,String> imageMap = new HashMap<>();
        for(int i=0; i<imageUrls.size(); i++){
            imageMap.put(i+1, imageUrls.get(i));
        }
        JsonNode jsonNode = objectMapper.valueToTree(imageMap);
        configurationRepository.save(Configuration.builder().data(jsonNode).type(ConfigurationKey.CAROUSEL_KEY).build());
        return imageUrls;
    }

    public void sendEmail() {
        User user = User.builder().email("vijoybardhan3@gmail.com").firstname("Vijay").build();
        Book book = new Book(1,"Java Programming","Vijay Vardhan",2021,5,5,"CS");
        Loan loan = Loan.builder().book(book).issueDate(LocalDate.now()).dueDate(LocalDate.of(2025,3,21)).build();
        smtpService.sendEmail(user,"Book borrowed","Vijoy you have issued a book",loan);
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
}
