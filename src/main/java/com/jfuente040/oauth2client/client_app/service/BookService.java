package com.jfuente040.oauth2client.client_app.service;

import com.jfuente040.oauth2client.client_app.persistence.entity.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book save(Book book);
    List<Book> findAll();
    Optional<Book> findById(Long id);
    Book update(Long id, Book book);
    void deleteById(Long id);
}
