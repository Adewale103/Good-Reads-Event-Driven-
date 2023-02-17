package africa.semicolon.goodreads.repository;

import africa.semicolon.goodreads.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    Book findBookByTitleIsIgnoreCase(String title);

    List<Book> findBookByUploadedBy(String email);

    Page<Book> findAll(Pageable pageable);
}
