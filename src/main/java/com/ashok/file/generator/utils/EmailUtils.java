package com.ashok.file.generator.utils;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtils {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailUtils(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendEmail(File file) {
        boolean status = false;

        if (file == null || !file.exists()) {
            System.err.println("File is null or does not exist.");
            return status;
        }

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            
            helper.setTo("shuklaram17@gmail.com");
            helper.setSubject("Your Report");
            helper.setText("<h2>Please download your report</h2>", true);

            helper.addAttachment(file.getName(), file);
            mailSender.send(msg);

            status = true;
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
        }

        return status;
    }
}
