package com.devbueno.library.api.service.impl;

import com.devbueno.library.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${application.mail.default-remetent}")
    private String remetent;

    @Override
    public void sendMails(String message, List<String> mailsList) {
        String[] mails = mailsList.toArray(new String[mailsList.size()]);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent);
        mailMessage.setSubject("LIVRO COM EMPRESTIMO ATRASADO!");
        mailMessage.setText(message);
        mailMessage.setTo(mails);
        javaMailSender.send(mailMessage);
    }
}
