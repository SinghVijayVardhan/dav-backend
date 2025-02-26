package org.dav.services;

import com.google.api.client.http.InputStreamContent;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import org.dav.entity.Configuration;
import org.dav.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@Slf4j
@Service
public class GoogleDriveService {
    private final ConfigurationRepository configurationRepository;
    private final String folderId;

    @Autowired
    public GoogleDriveService(ConfigurationRepository configurationRepository,@Value("${google.drive.folder}") String folderId) {
        this.configurationRepository = configurationRepository;
        this.folderId = folderId;
    }

    public Drive getDriveService() throws IOException {
        String SERVICE_ACCOUNT_JSON_KEY = "GOOGLE_DRIVE";
        Configuration configuration = configurationRepository.findConfigurationByType(SERVICE_ACCOUNT_JSON_KEY);
        if(configuration==null){
            log.warn("Set up google drive configuration");
            return null;
        }
        String SERVICE_ACCOUNT_JSON = configuration.getData().asText();
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(SERVICE_ACCOUNT_JSON))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(new com.google.api.client.http.javanet.NetHttpTransport(),
                new com.google.api.client.json.gson.GsonFactory(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("dav-ispat").build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        Drive driveService = getDriveService();
        if (driveService == null) {
            return null;
        }

        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setParents(Collections.singletonList(folderId));

        InputStream fileInputStream = new ByteArrayInputStream(file.getBytes());
        InputStreamContent mediaContent = new InputStreamContent(file.getContentType(), fileInputStream);

        File uploadedFile = driveService.files()
                .create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        return uploadedFile.getWebViewLink();
    }

    public void deleteFile(String fileUrl) throws IOException {
        Drive driveService = getDriveService();
        if (driveService == null) {
            return;
        }
        String fileId = extractFileId(fileUrl);
        if (fileId == null) {
            System.out.println("Invalid file URL: " + fileUrl);
            return;
        }

        driveService.files().delete(fileId).execute();
        System.out.println("File deleted: " + fileId);
    }

    private String extractFileId(String fileUrl) {
        String regex = """
                https://drive.google.com/file/d/([a-zA-Z0-9_-]+)/view""";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(fileUrl);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
