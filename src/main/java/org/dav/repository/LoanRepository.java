package org.dav.repository;

import org.dav.entity.Loan;
import org.dav.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {
    Page<Loan> findAllByUser(User user, Pageable pageable);

    List<Loan> findAllByUserAndReturnDate(User user, LocalDate date);

    Page<Loan> findAllByUserIn(List<User> users, Pageable pageable);
}
