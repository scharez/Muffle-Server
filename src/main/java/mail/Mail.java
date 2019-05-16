package mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

public class Mail {

    private String emailPassword;
    private Session session;


    private void transport(Message message, String mailBody) throws MessagingException {
        message.setContent(mailBody, "text/html");

        Transport transport = session.getTransport("smtp");
        transport.connect("smtp.gmail.com", "dr.boozeteam@gmail.com", emailPassword);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
