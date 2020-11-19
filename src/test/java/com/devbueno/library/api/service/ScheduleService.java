package com.devbueno.library.api.service;

import com.devbueno.library.api.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private LoanService loanService;
    @Autowired
    private EmailService emailService;

    @Value("${application.mail.lateloans.message}")
    private String message;

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans() {
        List<Loan> allLateLoans = loanService.getAllLateLoans();

        List<String> mailsList = allLateLoans.stream()
                .map(loan -> loan.getEmail())
                .collect(Collectors.toList());

        emailService.sendMails(message, mailsList);
    }
}
