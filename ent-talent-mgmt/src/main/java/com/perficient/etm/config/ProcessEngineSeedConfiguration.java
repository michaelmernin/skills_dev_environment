package com.perficient.etm.config;

import org.activiti.spring.boot.DataSourceProcessEngineAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.perficient.etm.config.seed.SeedProcesses;

@Configuration
@AutoConfigureAfter(DataSourceProcessEngineAutoConfiguration.class)
public class ProcessEngineSeedConfiguration {
    
    private final Logger log = LoggerFactory.getLogger(ProcessEngineSeedConfiguration.class);
    
    @Bean
    @Profile({Constants.SPRING_PROFILE_DEVELOPMENT, Constants.SPRING_PROFILE_TEST, Constants.SPRING_PROFILE_UAT})
    public SeedProcesses seedProcesses() {
        SeedProcesses activiti = new SeedProcesses();
        log.debug("Creating activiti processes for seeded entities");
        return activiti;
    }
}
