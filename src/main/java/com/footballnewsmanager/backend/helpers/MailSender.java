package com.footballnewsmanager.backend.helpers;

import org.springframework.mail.SimpleMailMessage;

public class MailSender {


    public static SimpleMailMessage createMail(String subject, String text, String to){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailMessage.setTo(to);
        return mailMessage;
    }

}
