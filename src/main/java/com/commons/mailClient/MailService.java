package com.commons.mailClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.send.username:peeyushy95@gmail.com}")
    private String sendToId;

    public void sendMail(final String subject, final String body){
        final MimeMessage msg = javaMailSender.createMimeMessage();

        try {
            final MimeMessageHelper helper = new MimeMessageHelper(msg);

            helper.setTo(sendToId);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(msg);
        } catch (MessagingException e){
            log.error(e.getMessage());
        }

    }
}
