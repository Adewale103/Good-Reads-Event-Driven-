package africa.semicolon.goodreads.config;

import africa.semicolon.goodreads.models.User;
import africa.semicolon.goodreads.models.enums.RoleType;
import africa.semicolon.goodreads.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
@Slf4j
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (userRepository.findUserByEmail("adminuser@gmail.com").isEmpty()){
            User user = new User("Admin", "User","adminuser@gmail.com", passwordEncoder.encode("password1234#"), RoleType.ROLE_ADMIN);
            user.setDateJoined(LocalDate.now());
            userRepository.save(user);
        }
    }
}
