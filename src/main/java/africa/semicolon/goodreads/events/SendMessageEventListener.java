package africa.semicolon.goodreads.events;


import africa.semicolon.goodreads.models.MailResponse;
import africa.semicolon.goodreads.models.verificationMessageRequest;
import africa.semicolon.goodreads.service.EmailService;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.ExecutionException;

@Component
public class SendMessageEventListener{
    @Qualifier("mailgun_sender")

    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public SendMessageEventListener(EmailService emailService, TemplateEngine templateEngine) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }




    @EventListener
    public void handleSendMessageEvent(SendMessageEvent event) throws UnirestException, ExecutionException, InterruptedException{
        verificationMessageRequest messageRequest = (verificationMessageRequest) event.getSource();

        Context context = new Context();
        context.setVariable("user_name",messageRequest.getUserFullName());
        context.setVariable("verification_token", "https://www.google.com");
        messageRequest.setBody(templateEngine.process("registration_verification_mail.html",context));
        //messageRequest.setBody("Hello, I would like to verify your mail!");
        MailResponse mailResponse = emailService.sendSimpleMail((verificationMessageRequest) event.getSource()).get();
    }
}
