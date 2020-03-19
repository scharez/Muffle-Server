package mail;

import entity.Muffler;
import entity.VerificationToken;
import utils.PropertyUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class Mail {

    private Session session;


    private Properties message_prop;
    private Properties config_prop;

    public Mail() {

        Properties prop = System.getProperties();
        config_prop = PropertyUtil.getInstance().getConfigProps();
        message_prop = PropertyUtil.getInstance().getMessageProps();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", config_prop.getProperty("mail.host"));
        prop.put("mail.smtp.port", config_prop.getProperty("mail.port"));
        prop.put("mail.smtp.ssl.trust", config_prop.getProperty("mail.host"));

        session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config_prop.getProperty("mail.user"), config_prop.getProperty("mail.password"));
            }
        });

    }

    public void sendConfirmation(VerificationToken verificationToken, Muffler user) {

        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(config_prop.getProperty("mail.user")));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            message.setSubject(message_prop.getProperty("mail.activate.subject"));

            String mailBody = "<html><head> <meta charset=\"UTF-8\"> <style> * { font-family: \"Roboto\", \"Helvetica Neue\", sans-serif; text-align: center } body { background-image: url(\"https://muffle.scharez.at/assets/web/background.jpg\"); background-repeat: no-repeat; background-size: cover } .middlePosition { width: 50%; height: 45vh; position: absolute; left: 50%; top: 30%; transform: translate(-50%, -50%); border-radius: 2em; padding: 2em } .middlePosition h1 { color: #ffddab } .middlePosition p { color: rgba(255, 221, 171, 0.7) } .middlePosition button { border-radius: 4em; border: 1px solid #ffab2d; opacity: .6; transition: opacity .4s; background-color: #ffab2d; padding: .8em; color: white; margin-top: 20px; font-size: 15px; cursor: pointer;} </style></head><body> <div class=\"middlePosition\"> <img src=\"https://muffle.scharez.at/assets/web/logo.svg\" width=\"40%\"> <h1>" + user.getUsername().toUpperCase() + ", verify your Email</h1> <a href=\"localhost:8080/rest/muffle/verify?token=" + verificationToken.getToken() + "\"><button>Verify</button></a> </div></body></html>";

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(mailBody, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
