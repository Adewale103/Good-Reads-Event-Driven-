package africa.semicolon.goodreads.models;

import lombok.*;

import javax.validation.constraints.Email;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class verificationMessageRequest {
    @Email
    private String sender;
    @Email
    private String receiver;
    private String subject;
    private String body;
    private String userFullName;
    private String verificationToken;
    private String domainUrl;
}
