package com.jfuente040.oauth2client.client_app.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jfuente040.oauth2client.client_app.persistence.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Puedes agregar métodos personalizados aquí si lo necesitas
}
