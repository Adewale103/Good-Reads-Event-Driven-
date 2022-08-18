package africa.semicolon.goodreads.service;

import africa.semicolon.goodreads.controllers.requestsAndResponses.AccountCreationRequest;
import africa.semicolon.goodreads.controllers.requestsAndResponses.UpdateRequest;
import africa.semicolon.goodreads.dtos.UserDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.models.User;

import java.util.List;

public interface UserService {
    UserDto createUserAccount(String host, AccountCreationRequest request) throws GoodReadsException;
    UserDto findById(String userId) throws GoodReadsException;
    List<UserDto> findAll();
    UserDto updateUserProfile(String userId,UpdateRequest updateRequest) throws GoodReadsException;
    User findUserByEmail(String email) throws GoodReadsException;

    void verifyUser(String token) throws GoodReadsException;
}
