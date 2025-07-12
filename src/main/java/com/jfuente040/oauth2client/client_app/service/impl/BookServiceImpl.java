package com.jfuente040.oauth2client.client_app.service.impl;

import com.jfuente040.oauth2client.client_app.persistence.entity.Book;
import com.jfuente040.oauth2client.client_app.persistence.repository.BookRepository;
import com.jfuente040.oauth2client.client_app.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Book update(Long id, Book book) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    existingBook.setTitle(book.getTitle());
                    existingBook.setAuthor(book.getAuthor());
                    existingBook.setIsbn(book.getIsbn());
                    return bookRepository.save(existingBook);
                })
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
