import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.MessagingException;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

public class EmailAuthentication {

    /**
     * Creates a MimeMessage using the parameters provided.
     * @param sender    Email address of the sender
     * @param recipient Email address of the recipient
     * @param message   Message to be sent
     * @return          MimeMessage to be used to send email
     * @throws MessagingException if sad
     */
    public static MimeMessage createEmail(String sender, String recipient, String subject, String message) throws MessagingException {
        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(sender));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
        email.setSubject(subject);
        email.setText(message);

        return email;
    }

    /**
     * Create a message from an email.
     *
     * @param email Email to be set to raw of message
     * @return      Message containing base64url encoded email.
     * @throws MessagingException
     * @throws IOException
     */
    public static Message createEmailMessage(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}
