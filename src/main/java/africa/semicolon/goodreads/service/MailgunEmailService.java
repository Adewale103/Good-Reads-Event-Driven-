package africa.semicolon.goodreads.service;

import africa.semicolon.goodreads.models.MailResponse;
import africa.semicolon.goodreads.models.verificationMessageRequest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("mailgun_sender")
@NoArgsConstructor
public class MailgunEmailService implements EmailService {

    private final String DOMAIN = "sandboxbf9f5952afbd4424891ca8d0fc8e919f.mailgun.org";
    private final String PRIVATE_KEY = "17e0810ab30bcb31c4ed6edc6a421c9e-1b3a03f6-fc8429a2";
//            System.getenv("DOMAIN");
            //System.getenv("MAILGUN_PRIVATE_KEY");



    @Override
    @Async
    public CompletableFuture<MailResponse> sendSimpleMail(verificationMessageRequest messageRequest) throws UnirestException {
        log.info("DOMAIN -> {}", DOMAIN);
        log.info("API KEY -> {}", PRIVATE_KEY);
        log.info(messageRequest.getBody());
        HttpResponse<String> request = Unirest.post("https://api.mailgun.net/v3/" + DOMAIN + "/messages")
                .basicAuth("api", PRIVATE_KEY)
                .queryString("from", messageRequest.getSender())
                .queryString("to", messageRequest.getReceiver())
                .queryString("subject", messageRequest.getSubject())
                .queryString("html", messageRequest.getBody())
                .asString();
        MailResponse mailResponse = request.getStatus() == 200 ? new MailResponse(true) : new MailResponse(false);
        return CompletableFuture.completedFuture(mailResponse);
    }

    @Override
    public void sendHtmlMail(verificationMessageRequest messageRequest) {

    }
}
