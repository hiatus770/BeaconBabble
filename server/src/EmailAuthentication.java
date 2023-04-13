//import java.io.*;
//import java.util.*;
//import javax.mail.*;
//import javax.mail.internet.*;
//import javax.mail.MessagingException;
//
//
//import com.google.api.client.googleapis.json.*;
//import com.google.api.client.http.HttpRequestInitializer;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.gmail.*;
//import com.google.api.services.gmail.model.Message;
//import com.google.auth.http.HttpCredentialsAdapter;
//import com.google.auth.oauth2.GoogleCredentials;
//import org.apache.commons.codec.binary.Base64;
//
//
//public class EmailAuthentication {
//
//    public String sender;
//    public String recipient;
//    public String subject;
//    public String message;
//
//    /**
//     * Creates a MimeMessage using the parameters provided.
//     * @param sender    Email address of the sender
//     * @param recipient Email address of the recipient
//     * @param message   Message to be sent
//     * @return          MimeMessage to be used to send email
//     * @throws MessagingException if sad
//     */
//    public MimeMessage createEmail(String sender, String recipient, String subject, String message) throws MessagingException {
//        Properties properties = new Properties();
//        Session session = Session.getDefaultInstance(properties, null);
//
//        MimeMessage email = new MimeMessage(session);
//
//        email.setFrom(new InternetAddress(sender));
//        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
//        email.setSubject(subject);
//        email.setText(message);
//
//        return email;
//    }
//
//    /**
//     * Create a message from an email.
//     *
//     * @param email Email to be set to raw of message
//     * @return      Message containing base64url encoded email.
//     * @throws MessagingException
//     * @throws IOException
//     */
//    public Message createEmailMessage(MimeMessage email) throws MessagingException, IOException {
//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//        email.writeTo(buffer);
//        byte[] bytes = buffer.toByteArray();
//        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
//        Message message = new Message();
//        message.setRaw(encodedEmail);
//        return message;
//    }
//
//    /**
//     * Send an email from the user's mailbox to its recipient.
//     * Utilizes createEmail and createEmailMessage to create the message to be sent.
//     * @param sender
//     * @param recipient
//     * @param subject
//     * @param message
//     * @return
//     * @throws MessagingException
//     * @throws IOException
//     */
//    public Message sendEmail(String sender, String recipient, String subject, String message) throws MessagingException, IOException {
//
//        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(GmailScopes.GMAIL_SEND);
//        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
//
//        // Create Gmail API Client
//        Gmail service = new Gmail.Builder(new NetHttpTransport(), new GsonFactory(), requestInitializer)
//                .setApplicationName("Gmail API Java Quickstart")
//                .build();
//
//        // Encode as MIME message
//        MimeMessage emailMime = this.createEmail(sender, recipient, subject, message);
//
//        // Encode MIME message into gmail message
//        Message email = this.createEmailMessage(emailMime);
//
//        try {
//            email = service.users().messages().send("me", email).execute();
//            System.out.println("Message id: " + email.getId());
//            System.out.println(email.toPrettyString());
//            return email;
//        } catch (GoogleJsonResponseException e) {
//            GoogleJsonError error = e.getDetails();
//            switch (error.getCode()) {
//                case 401 -> System.out.println("Error 401: Invalid credentials");
//                case 403 -> System.out.println("Error 403: User does not have permission to send email");
//                default -> System.out.println("An error occurred: " + error.getMessage());
//            }
//            return null;
//        }
//    }
//}
