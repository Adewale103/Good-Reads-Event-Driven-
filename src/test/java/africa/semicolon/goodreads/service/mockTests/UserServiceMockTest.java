package africa.semicolon.goodreads.service.mockTests;

import africa.semicolon.goodreads.controllers.requestsAndResponses.AccountCreationRequest;
import africa.semicolon.goodreads.dtos.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.models.User;
import africa.semicolon.goodreads.repository.UserRepository;
import africa.semicolon.goodreads.security.jwt.TokenProvider;
import africa.semicolon.goodreads.service.UserService;
import africa.semicolon.goodreads.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserServiceMockTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    private UserService userService;
    private final String host = "http://www.localhost:8080/sigup";

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    private void setUp(){
        userService = new UserServiceImpl(userRepository, mapper, applicationEventPublisher, bCryptPasswordEncoder,tokenProvider);
    }
    @Test
    public void testThatUserCanCreateAccount() throws GoodReadsException{

        AccountCreationRequest accountCreationRequest =
                new AccountCreationRequest("Firstname", "Lastname", "testemail@gmail.com","password" );

        User userToReturn = User.builder()
                .id(1L)
                .firstName(accountCreationRequest.getFirstName())
                .lastName(accountCreationRequest.getLastName())
                .email(accountCreationRequest.getEmail())
                .password(accountCreationRequest.getPassword())
                .build();
        UserDto userDtoToReturn = UserDto.builder()
                .id(1L)
                .firstName(accountCreationRequest.getFirstName())
                .lastName(accountCreationRequest.getLastName())
                .email(accountCreationRequest.getEmail())
                .build();

        when(userRepository.findUserByEmail("testemail@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(userToReturn);
        when(mapper.map(userToReturn, UserDto.class)).thenReturn(userDtoToReturn);
        when(bCryptPasswordEncoder.encode(userToReturn.getPassword())).thenReturn(userToReturn.getPassword());
        UserDto userDto = userService.createUserAccount(host,accountCreationRequest);
        verify(userRepository,times(1)).findUserByEmail("testemail@gmail.com");

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getFirstName()).isEqualTo("Firstname");
        assertThat(userDto.getLastName()).isEqualTo("Lastname");
        assertThat(userDto.getEmail()).isEqualTo("testemail@gmail.com");
    }

    @Test
    public void testThatUserEmailIsUnique() throws GoodReadsException{
        AccountCreationRequest accountCreationRequest =
                new AccountCreationRequest("Firstname", "Lastname", "testemail@gmail.com","password" );
        UserDto userDtoToReturn = UserDto.builder()
                .id(1L)
                .firstName(accountCreationRequest.getFirstName())
                .lastName(accountCreationRequest.getLastName())
                .email(accountCreationRequest.getEmail())
                .build();

        User userToReturn = User.builder()
                .id(1L)
                .firstName(accountCreationRequest.getFirstName())
                .lastName(accountCreationRequest.getLastName())
                .email(accountCreationRequest.getEmail())
                .password(accountCreationRequest.getPassword())
                .build();

        when(userRepository.findUserByEmail("testemail@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(userToReturn);
        when(mapper.map(userToReturn, UserDto.class)).thenReturn(userDtoToReturn);
        when(bCryptPasswordEncoder.encode(userToReturn.getPassword())).thenReturn(userToReturn.getPassword());
        UserDto userDto = userService.createUserAccount(host,accountCreationRequest);
        verify(userRepository,times(1)).findUserByEmail("testemail@gmail.com");

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getFirstName()).isEqualTo("Firstname");
        assertThat(userDto.getLastName()).isEqualTo("Lastname");
        assertThat(userDto.getEmail()).isEqualTo("testemail@gmail.com");

        AccountCreationRequest accountCreationRequest2 =
                new AccountCreationRequest("Wale", "Paul", "testemail@gmail.com","password" );

        when(userRepository.findUserByEmail("testemail@gmail.com")).thenReturn(Optional.of(userToReturn));

        assertThatThrownBy(()->userService.createUserAccount(host, accountCreationRequest2))
                .isInstanceOf(GoodReadsException.class)
                .hasMessage("user already exists!");
        verify(userRepository,times(2)).findUserByEmail("testemail@gmail.com");

    }
    @Test
    public void testThatUserCanBeFoundById() throws GoodReadsException{

        User userToReturn = User.builder()
                .id(1L)
                .firstName("Amaka")
                .lastName("Azuzu")
                .email("aaka@gmail.com")
                .password("23455")
                .build();
        UserDto userDtoToReturn = UserDto.builder()
                .id(1L)
                .firstName("Amaka")
                .lastName("Azuzu")
                .email("aaka@gmail.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userToReturn));
        when(mapper.map(userToReturn, UserDto.class)).thenReturn(userDtoToReturn);

        UserDto userDto = userService.findById("1");
        verify(userRepository,times(1)).findById(1L);

        assertThat("Amaka").isEqualTo(userDto.getFirstName());
        assertThat(1L).isEqualTo(userDto.getId());
        assertThat("Azuzu").isEqualTo(userDto.getLastName());
        assertThat("aaka@gmail.com").isEqualTo(userDto.getEmail());

    }
}
