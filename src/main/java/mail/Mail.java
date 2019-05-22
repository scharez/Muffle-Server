package mail;

import entity.Muffler;
import entity.VerificationToken;
import helper.PropertyLoader;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class Mail {

    private Session session;

    private String url = "";

    private PropertyLoader pl = new PropertyLoader();

    public Mail() {

        Properties prop = System.getProperties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "mail.scharez.at");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.ssl.trust", "mail.scharez.at");

        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(pl.prop.getProperty("mail.user"), pl.prop.getProperty("mail.password"));
            }
        });

    }

    public void sendConfirmation(VerificationToken verificationToken, Muffler user) {

        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(pl.prop.getProperty("mail.user")));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            message.setSubject("Activate your Muffle-Account");

            String mailBody =
                    "Hello " + user.getUsername() + " Please confirm your email<br>" +
                            "<a href='" + "http://localhost:8080/rest/muffle/verify/?token=" + verificationToken.getToken()
                            + "'>Confirm your email</a>";

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(mailBody, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
