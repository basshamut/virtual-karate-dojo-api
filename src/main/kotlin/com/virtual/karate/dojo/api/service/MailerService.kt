package com.virtual.karate.dojo.api.service

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.InputStreamSource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailerService @Autowired constructor(
    private val mailSender: JavaMailSender
) {
    fun sendMail(to: String, subject: String, text: String, html: String, attachment: ByteArray?) {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setFrom("no-reply@mushindojo.com")
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(text, html)

        attachment?.let {
            val attachmentSource: InputStreamSource = ByteArrayResource(it)
            helper.addAttachment("invoice.pdf", attachmentSource)
        }

        mailSender.send(message)
    }

    fun sendInvoice(userEmail: String, invoiceData: ByteArray) {

        sendMail(
            to = userEmail,
            subject = "Welcome to Mushin Dojo",
            text = "Welcome to Mushin Dojo",
            html = "<h1>Welcome to Mushin Dojo</h1><p>Invoice from Mushin Dojo payment :)</p>",
            attachment = invoiceData
        )
    }
}