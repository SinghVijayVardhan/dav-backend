package org.dav.repository;

import org.dav.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {

    Configuration findConfigurationByType (String type);
}
