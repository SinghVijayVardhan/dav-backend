package org.dav.entity;

import jakarta.persistence.*;
import lombok.*;
import org.dav.enums.FineStatus;


import java.time.LocalDate;

@Entity(name = "loans")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user ;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "fine_amount")
    private Double fineAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "fine_status")
    private FineStatus fineStatus;
}
