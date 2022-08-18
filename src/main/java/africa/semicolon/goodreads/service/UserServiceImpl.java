package africa.semicolon.goodreads.service;

import africa.semicolon.goodreads.controllers.requestsAndResponses.AccountCreationRequest;
import africa.semicolon.goodreads.controllers.requestsAndResponses.UpdateRequest;
import africa.semicolon.goodreads.dtos.UserDto;
import africa.semicolon.goodreads.events.SendMessageEvent;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.models.Role;
import africa.semicolon.goodreads.models.verificationMessageRequest;
import africa.semicolon.goodreads.models.User;
import africa.semicolon.goodreads.repository.UserRepository;
import africa.semicolon.goodreads.security.jwt.TokenProvider;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper, ApplicationEventPublisher applicationEventPublisher,
                           BCryptPasswordEncoder bCryptPasswordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.modelMapper = mapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public UserDto createUserAccount(String host, AccountCreationRequest accountCreationRequest)  throws GoodReadsException {
        validate(accountCreationRequest, userRepository);
        User user = new User(accountCreationRequest.getFirstName(), accountCreationRequest.getLastName(),
                accountCreationRequest.getEmail(), bCryptPasswordEncoder.encode(accountCreationRequest.getPassword()));
        user.setDateJoined(LocalDate.now());
        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateTokenForVerification(String.valueOf(savedUser.getId()));

        verificationMessageRequest messageRequest = verificationMessageRequest.builder()
                .subject("VERIFY EMAIL")
                .sender("adeyinkawale13@gmail.com")
                .receiver(user.getEmail())
                .domainUrl(host)
                .verificationToken(token)
                .userFullName(String.format("%s %s",user.getFirstName(),user.getLastName()))
                .build();
        SendMessageEvent event = new SendMessageEvent(messageRequest);

        applicationEventPublisher.publishEvent(event);
        return modelMapper.map(savedUser,UserDto.class);
    }

    @Override
    public UserDto findById(String userId) throws GoodReadsException {
        User foundUser = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(()-> new GoodReadsException(String.format("""
                        User with Id %s not found""",userId),404));

        return modelMapper.map(foundUser,UserDto.class);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user,UserDto.class)).toList();
    }

    @Override
    public UserDto updateUserProfile(String userId, UpdateRequest updateRequest) throws GoodReadsException {
        User foundUser = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(()-> new GoodReadsException(String.format("""
                        User with Id %s not found""",userId),404));

        User userToSave = modelMapper.map(updateRequest, User.class);
        userToSave.setId(foundUser.getId());
        userToSave.setPassword(foundUser.getPassword());
        userToSave.setDateJoined(foundUser.getDateJoined());
        userToSave.setVerified(foundUser.isVerified());
        userToSave.setRoles(foundUser.getRoles());
        userRepository.save(userToSave);
        return modelMapper.map(userToSave,UserDto.class);

    }

    @Override
    public User findUserByEmail(String email) throws GoodReadsException {
        return userRepository.findUserByEmail(email).orElseThrow(()-> new GoodReadsException("user not found", 400));
    }

    @Override
    public void verifyUser(String token) throws GoodReadsException {
        Claims claims = tokenProvider.getAllClaimsFromJWTToken(token);
        Function<Claims, String> getSubjectFromClaim = Claims::getSubject;
        Function<Claims, Date> getExpirationDateFromClaim = Claims::getExpiration;
        Function<Claims, Date> getIssuedAtDateFromClaim= Claims::getIssuedAt;

        String userId = getSubjectFromClaim.apply(claims);
        if (userId == null){
            throw new GoodReadsException("User id not present in verification token", 404);
        }
        Date expiryDate = getExpirationDateFromClaim.apply(claims);
        if (expiryDate == null){
            throw new GoodReadsException("Expiry Date not present in verification token", 404);
        }
        Date issuedAtDate = getIssuedAtDateFromClaim.apply(claims);

        if (issuedAtDate == null){
            throw new GoodReadsException("Issued At date not present in verification token", 404);
        }

        if (expiryDate.compareTo(issuedAtDate) > 14.4 ){
            throw new GoodReadsException("Verification Token has already expired", 404);
        }

        User user = findUserByIdInternal(userId);
        if(user == null){
            throw new GoodReadsException("User id does not exist",404);
        }
        user.setVerified(true);
        userRepository.save(user);
    }

    private User findUserByIdInternal(String userId) {
        return userRepository.findById(Long.valueOf(userId)).orElse(null);
    }

    private static void validate(AccountCreationRequest request, UserRepository userRepository) throws GoodReadsException {
        User foundUser = userRepository.findUserByEmail(request.getEmail()).orElse(null);
        if(foundUser != null){
            throw new GoodReadsException("user already exists!",400);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.findUserByEmail(username).orElse(null);

       if(user != null){
           return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),getAuthorities(user.getRoles()));
       }
       return null;
    }
    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleType().name())).collect(Collectors.toSet());
    }
}
