package mail;

import helper.PropertyLoader;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

public class Mail {

    private PropertyLoader pl = new PropertyLoader();

    public Mail() {


        this.emailUser = pl.prop.getProperty("mail.user");
        this.emailPassword = pl.prop.getProperty("mail.password");
    }

    private String emailUser;
    private String emailPassword;
    private Session session;


    private void transport(Message message, String mailBody) throws MessagingException {
        message.setContent(mailBody, "text/html");

        Transport transport = session.getTransport("smtp");
        transport.connect("mail.scharez.at", "muffle@scharez.at", emailPassword);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
