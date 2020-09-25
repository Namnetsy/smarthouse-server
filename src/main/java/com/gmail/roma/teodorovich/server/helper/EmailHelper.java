package com.gmail.roma.teodorovich.server.helper;

import com.gmail.roma.teodorovich.server.Config;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;
import java.security.SecureRandom;

public class EmailHelper {

    public static String generateVerificationCode() {
        char[] allowedChars = "QWERTYUPAFHJKLXCVNM1234567890".toCharArray();
        SecureRandom random = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            codeBuilder.append(allowedChars[random.nextInt(allowedChars.length)]);
        }

        return codeBuilder.toString();
    }

    public static boolean sendEmail(String email, String subject, String body) {
        Email from = new Email("Oda@good.startup");
        Email to = new Email(email);
        Content content = new Content("text/html", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(Config.getSendgridToken());
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            return true;
        } catch (IOException ex) {
            ex.printStackTrace();

            return false;
        }
    }

}
