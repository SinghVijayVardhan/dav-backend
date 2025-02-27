package org.dav.repository;

import org.dav.entity.Book;
import org.dav.enums.ReservationStatus;
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

    @Query(value = "SELECT * FROM books b WHERE b.title LIKE ?1 AND b.category LIKE ?2", nativeQuery = true)
    Page<Book> findAllTitleAndCategoryContaining(String name, String category, Pageable pageable);

    @Query(value = "SELECT * FROM books b WHERE b.category LIKE ?1",nativeQuery = true)
    Page<Book> findAllCategoryContaining(String category, Pageable pageable);

    @Query(value = "SELECT DISTINCT(category) FROM books",nativeQuery = true)
    List<String> findAllCategories();

    @Query(value = "SELECT b.* FROM books b INNER JOIN loans l ON l.book_id = b.id WHERE l.user_id = ?1 AND l.return_date=null",nativeQuery = true)
    List<Book> findAllBookIssuedByUser(Integer id);

    @Query(value = "SELECT b.* FROM books b INNER JOIN reservations r ON r.book_id = b.id WHERE r.user_id = ?1 AND r.status in ?2",nativeQuery = true)
    List<Book> findAllBookReservedByUserAndStatus(Integer id, List<String> reservationStatus);
}
