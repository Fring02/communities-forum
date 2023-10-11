package com.cloud.communitiesservice.config;

import com.cloud.communitiesservice.util.DatabaseSeeder;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper mapper(){
        return new ModelMapper();
    }
    @Bean
    public CommandLineRunner commandLineRunner(DatabaseSeeder seeder){
        return args -> seeder.seedRoles();
    }
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public CommonsRequestLoggingFilter loggingFilter(){
        var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("Request details: ");
        return filter;
    }
}
