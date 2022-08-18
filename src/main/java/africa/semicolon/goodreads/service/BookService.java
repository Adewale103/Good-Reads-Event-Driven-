package africa.semicolon.goodreads.service;

import africa.semicolon.goodreads.controllers.requestsAndResponses.BookItemUploadRequest;
import africa.semicolon.goodreads.dtos.BookDto;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import africa.semicolon.goodreads.models.Book;
import africa.semicolon.goodreads.models.Credentials;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface BookService {
    CompletableFuture<Map<String, Credentials>> generateUploadURLs(String fileExtension, String imageExtension) throws ExecutionException, InterruptedException;
    Book save(BookItemUploadRequest bookItemUploadRequest);
    Book findBookByTitle(String title) throws GoodReadsException;
    Map<String, String> generateDownloadURLs(String fileName, String imageName) throws GoodReadsException;
    Map<String, Object> findAll(int numberOfPages, int numberOfItems);
    List<BookDto> getAllBooksForUser(String email);
}
