package com.footballnewsmanager.backend.helpers;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

public class MailSender {


    public static MimeMessage createResetPassMail(String subject, String token, String to, JavaMailSender mailSender) throws MessagingException {
        String title = "<h1><b><i>Football News Manager</i></b></h1>";
        String resetPass = "<h2 style='margin: 0px;'>Reset hasła</h2>";
        String logo = "<img src='cid:logo' height='100' width='100' />";
        String generatedToken = "<h2 style='margin-top:0px;'>Wygenerowany token:</h2>";
        String tokenHtml = "<h3 style='margin: 0px;'>" + token + "</h3>";
        String goToApp = "<br><button class='button button-primary'><a href='https://www.footballnewsmanager.com/reset?token="+token+"'>Przejdź do aplikacji</a></button>";
        String tokenExpiration = "<h4 style='margin-bottom: 0px;'>Token jest ważny przez 24 godziny</h4>";
        String noreplyMessage = "<h4 style='margin: 0px;'>Wiadomość wygenerowana. Prosimy na nią nie odpowiadać</h4>";
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        String htmlText = "<head>" +
                buttonStyleFile() +
                "</head><body>" +
                "<div style='display: inline-block; margin: 24px;color:#fff; background-color: #006400; padding: 24px; border-radius: 6px; text-align:center;' >"
                + logo
                + title
                + resetPass
                + generatedToken
                + tokenHtml
                + goToApp
                + tokenExpiration
                + noreplyMessage
                + "</div></body></html>";
        try {
            mimeMessageHelper.setText(htmlText, true);
            mimeMessageHelper.setFrom("noreply@footballnewsmanager.com");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            File file = new File("/home/pi/football-news-manager/logo.png");
            mimeMessageHelper.addInline("logo", file);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return mimeMessage;
    }

    private static String buttonStyleFile() {
        return "<style type='text/css'>" +
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
                "    border: 2px solid #fff;\n" +
                "    background-color: white;\n" +
                "}\n" +
                "\n" +
                ".button-primary:hover{\n" +
                "    background-color: #fff;\n" +
                "    color: white;\n" +
                "} " +
                "        </style>";
    }


}
