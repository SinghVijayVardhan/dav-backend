package org.dav.repository;

import org.dav.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query(value = "SELECT * FROM books b WHERE b.title LIKE ?1", nativeQuery = true)
    Page<Book> findAllTitleContaining(String s, Pageable pageable);

    List<Book> findByTitleInAndPublicationYearIn(List<String> titles, List<Integer> years);
}
