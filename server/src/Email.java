import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class Email {
    String host, sender;
    Properties properties;

    public Email(String sender) {
        this.sender = sender;
        properties = new Properties();
        host = "localhost";
    }

    public void sendEmail(String recipient, String subject, String message) throws MessagingException {
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.user", sender);
        properties.setProperty("mail.password", "thehuangtherivas");

        Session session = Session.getDefaultInstance(properties, null);

        try {
            MimeMessage email = new MimeMessage(session);

            email.setFrom(new InternetAddress(sender));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
            email.setSubject(subject);
            email.setText(message);

            Transport.send(email);
            System.out.println("Email sent to " + recipient);
        } catch (MessagingException e) {
            System.out.println("Error sending email to " + recipient);
            e.printStackTrace();
        }
    }
}
