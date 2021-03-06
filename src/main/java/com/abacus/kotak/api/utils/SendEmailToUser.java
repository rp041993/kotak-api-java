/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.utils;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author abacus1
 */
public class SendEmailToUser {

    public static void sendMessage(String email, String subject, String body) {

        try {

            String host = "mail.abacussoftware.services";
            String user = "admin@abacussoftware.services";
            String pass = "abacus@2019";
            String from = "admin@abacussoftware.services";

            boolean sessionDebug = false;

            Properties props = System.getProperties();
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.required", "true");

            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            Session mailSession = Session.getInstance(props, null);
            mailSession.setDebug(sessionDebug);
            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(from));
            InternetAddress address = (new InternetAddress(email));
            msg.setRecipient(Message.RecipientType.TO, address);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
//                            msg.setText(messageText);
            msg.setContent(body, "text/html; charset=utf-8");
            Transport transport = mailSession.getTransport("smtp");
            transport.connect(host, user, pass);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
