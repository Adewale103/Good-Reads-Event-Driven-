package africa.semicolon.goodreads.events;


import africa.semicolon.goodreads.models.MailResponse;
import africa.semicolon.goodreads.models.verificationMessageRequest;
import africa.semicolon.goodreads.service.EmailService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class SendMessageEventListener{
    @Qualifier("mailgun_sender")

    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final Environment env;

    public SendMessageEventListener(EmailService emailService, TemplateEngine templateEngine, Environment env) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
        this.env = env;
    }

    @EventListener
    public void handleSendMessageEvent(SendMessageEvent event) throws UnirestException, ExecutionException, InterruptedException{
        verificationMessageRequest messageRequest = (verificationMessageRequest) event.getSource();

        String verificationLink = messageRequest.getDomainUrl()+"api/v1/auth/verify/"+messageRequest.getVerificationToken();

        log.info("Message request --> {}",messageRequest);
        Context context = new Context();
        context.setVariable("user_name", messageRequest.getUserFullName().toUpperCase());
        context.setVariable("verification_token", verificationLink);
        if (Arrays.asList(env.getActiveProfiles()).contains("prod")){
            log.info("Message Event -> {}", event.getSource());
            messageRequest.setBody(templateEngine.process("registration_verification_mail.html", context));
            MailResponse mailResponse = emailService.sendHtmlMail(messageRequest).get();
            log.info("Mail Response --> {}", mailResponse);
        } else{
            messageRequest.setBody(verificationLink);
            MailResponse mailResponse = emailService.sendSimpleMail(messageRequest).get();
            log.info("Mail Response simple --> {}", mailResponse);
        }
    }
}
