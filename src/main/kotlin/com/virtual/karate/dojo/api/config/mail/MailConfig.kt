package com.virtual.karate.dojo.api.config.mail

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class MailConfig {
    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = System.getenv("EMAIL_HOST") ?: "smtp.gmail.com"
        mailSender.port = 587
        mailSender.username = System.getenv("EMAIL_USER") ?: "default@example.com"
        mailSender.password = System.getenv("EMAIL_PASS") ?: "defaultpassword"

        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"
        props["mail.smtp.connectiontimeout"] = "60000"
        props["mail.smtp.timeout"] = "60000"
        props["mail.smtp.writetimeout"] = "60000"

        return mailSender
    }
}
