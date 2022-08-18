package africa.semicolon.goodreads.service;

import africa.semicolon.goodreads.models.MailResponse;
import africa.semicolon.goodreads.models.verificationMessageRequest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<MailResponse> sendSimpleMail(verificationMessageRequest messageRequest) throws UnirestException;
    void sendHtmlMail(verificationMessageRequest messageRequest);
}
