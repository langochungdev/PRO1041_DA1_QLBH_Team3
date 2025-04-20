package Utils;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.Properties;

public class MailSender {

    private static final String USERNAME = "langochungse23@gmail.com";
    private static final String PASSWORD = "xplz nxar drka bcxp";

    public static boolean sendEmail(String to, String subject, String content) {
        return sendEmail(to, subject, content, null);  // Gửi không đính kèm
    }

    public static boolean sendEmail(String to, String subject, String content, File attachmentFile) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            if (attachmentFile == null) {
                // Gửi email không có file đính kèm
                message.setText(content);
            } else {
                // Gửi email có đính kèm file
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(content);

                MimeBodyPart attachmentPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachmentFile);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(attachmentFile.getName());

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(attachmentPart);

                message.setContent(multipart);
            }

            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
