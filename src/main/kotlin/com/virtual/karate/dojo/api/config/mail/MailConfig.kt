package com.virtual.karate.dojo.api.config.mail

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class MailConfig {

    @Value("\${email.host}")
    private lateinit var emailHost: String

    @Value("\${email.user}")
    private lateinit var emailUser: String

    @Value("\${email.pass}")
    private lateinit var emailPass: String

    @Value("\${email.port}")
    private var emailPort: Int = 587

    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = emailHost
        mailSender.port = emailPort
        mailSender.username = emailUser
        mailSender.password = emailPass

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

