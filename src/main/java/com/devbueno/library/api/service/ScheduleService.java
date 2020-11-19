package com.devbueno.library.api.service;

import com.devbueno.library.api.model.entity.Loan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class ScheduleService {

    private static final String CRON_LATE_LOANS = " 0 0/1 * * * *";

    @Autowired
    private LoanService loanService;
    @Autowired
    private EmailService emailService;

    @Value("${application.mail.lateloans.message}")
    private String message;


    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans() {

        List<Loan> allLateLoans = loanService.getAllLateLoans();

        List<String> mailsList = allLateLoans.stream()
                .map(loan -> loan.getEmail())
                .collect(Collectors.toList());

        emailService.sendMails(message, mailsList);
        log.info("emails enviados,"+ mailsList);
    }


}
