package org.dav.repository;

import org.dav.entity.Book;
import org.dav.entity.Loan;
import org.dav.entity.Reservation;
import org.dav.entity.User;
import org.dav.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByBookAndUserAndStatus(Book book, User user, ReservationStatus status);

    Page<Reservation> findAllByUser(User user, Pageable pageable);

    Page<Reservation> findAllByUserIn(List<User> users, Pageable pageable);
}
