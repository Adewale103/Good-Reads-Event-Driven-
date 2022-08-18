package africa.semicolon.goodreads.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Credentials {
    private String fileName;
    private String uploadUrl;
}
