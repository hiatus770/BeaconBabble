import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.MessagingException;

public class EmailAuthentication {
    public String recipient;
    private String sender = "beaconauthentication@gmail.com";
    private String password = "thehuangtherivas";

    public EmailAuthentication() {

    }

    /**
     * Responsible for sending the email to the recipient.
     * @param recipient The email address of the recipient.
     * @param code The authentication code.
     */
    public void send(String recipient, int code) throws MessagingException {
        // Get properties object
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        // Get the Session object
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        // Compose the message
        // all the message properties
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject("Beacon Authentication Code");
        message.setText("This is your authentication code. Type it into the confirmation box. \n" + Integer.toString(code) + "If you did not request this code, please ignore this email.");

        // Send message
        Transport.send(message);
        System.out.println("Email sent.");
    }
}
