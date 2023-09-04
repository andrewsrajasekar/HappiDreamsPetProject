package com.happidreampets.app.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.constants.ProductConstants;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailService {
    private static final Logger LOG = Logger.getLogger(EmailService.class.getName());
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public JSONObject sendSimpleMail(EmailModel details) {
        try {
            JSONObject data = new JSONObject();
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            List<String> recipients = details.getRecipients();
            List<String> successRecipients = new ArrayList<>();
            HashMap<String, Exception> failedRecipientVsException = new HashMap<>();

            // Setting up necessary details
            helper.setFrom(sender);
            helper.setText(details.getMsgBody(), details.getIsHtml());
            helper.setSubject(details.getSubject());

            for (String recipient : recipients) {
                try {
                    helper.setTo(recipient);
                    javaMailSender.send(mimeMessage);
                    successRecipients.add(recipient);
                } catch (Exception ex) {
                    failedRecipientVsException.put(recipient, ex);
                }
            }

            data.put(ProductConstants.LowerCase.SUCCESS, successRecipients);
            data.put(ControllerConstants.LowerCase.ERRORS, failedRecipientVsException);
            return data;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Exception is ::: " + ex);
            return null;
        }

    }

}
