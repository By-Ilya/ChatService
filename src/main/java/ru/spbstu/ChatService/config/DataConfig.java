package ru.spbstu.ChatService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaRepositories(basePackages = "ru.spbstu.ChatService.repository")
@EnableTransactionManagement
@Configuration
public class DataConfig {
}
