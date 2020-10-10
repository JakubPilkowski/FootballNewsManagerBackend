package com.footballnewsmanager.backend.helpers;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class MailSender {


    public static MimeMessage createResetPassMail(String subject, String token, String to, JavaMailSender mailSender){
        String resetPass = "<h2 style='margin: 0px;'>Reset hasła</h2>";
        String generatedToken = "<h2 style='margin-top:0px;'>Wygenerowany token:</h2>";
        String tokenHtml = "<h3 style='margin: 0px;'>"+token+"</h3>";
        String goToApp = "<br><button class='button button-primary' >Przejdź do aplikacji</button>";
        String tokenExpiration = "<h4 style='margin-bottom: 0px;'>Token jest ważny przez 24 godziny</h4>";
        String noreplyMessage = "<h4 style='margin: 0px;'>Wiadomość wygenerowana. Prosimy na nią nie odpowiadać</h4>";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
        String htmlText = "<head>" +
                buttonStyleFile() +
                "</head><body>" +
                "<div style='display: inline-block; margin: 24px; border: 2px solid grey; padding: 24px; border-radius: 6px; text-align:center;' >"
                + "LOGO APLIKACJI"
                + resetPass
                + generatedToken
                + tokenHtml
                + goToApp
                + tokenExpiration
                + noreplyMessage
                + "</div></body></html>";
        try {
            mimeMessageHelper.setFrom("noreply@footballnewsmanager.com");
            mimeMessageHelper.setText(htmlText, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return mimeMessage;
    }

    private static String buttonStyleFile() {
        return    "<style type='text/css'>" +
                " .button {\n" +
                "    border-radius: 18px;\n" +
                "    padding: 6px 1rem;\n" +
                "    margin: 6px;\n" +
                "    outline: none;\n" +
                "}\n" +
                "\n" +
                ".button:active,\n" +
                ".button:focus{\n" +
                "    outline: none;\n" +
                "}\n" +
                ".button-primary {\n" +
                "    border: 2px solid #87CEEB;\n" +
                "    background-color: white;\n" +
                "}\n" +
                "\n" +
                ".button-primary:hover{\n" +
                "    background-color: #87CEEB;\n" +
                "    color: white;\n" +
                "} "+
                "        </style>";
    }


}
