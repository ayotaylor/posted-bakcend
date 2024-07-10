package com.posted.post.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTopicConfig.class);

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
          logger.warn("Bootstrap address: {}", bootstrapAddress);
          Map<String, Object> configs = new HashMap<>();
          configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
          return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postCreatedTopic() {
         return new NewTopic("postCreatedTopic", 1, (short) 1); // use configuration for values
    }

    @Bean
    public NewTopic postDeletedTopic() {
         return new NewTopic("postDeletedTopic", 1, (short) 1); // use configuration for values
    }

    @Bean
    public NewTopic postUpdatedTopic() {
         return new NewTopic("postUpdatedTopic", 1, (short) 1); // use configuration for values
    }

    @Bean
    public NewTopic postFavedTopic() {
         return new NewTopic("postFavedTopic", 1, (short) 1); // use configuration for values
    }

    @Bean
    public NewTopic postUnfavedTopic() {
         return new NewTopic("postUnfavedTopic", 1, (short) 1); // use configuration for values
    }

    @Bean
    public NewTopic postRepostedTopic() {
         return new NewTopic("postRepostedTopic", 1, (short) 1); // use configuration for values
    }

    @Bean
    public NewTopic postUndoRepostTopic() {
         return new NewTopic("postUndoRepostTopic", 1, (short) 1); // use configuration for values
    }
}
