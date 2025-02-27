package org.dav.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "issue_date")
    private LocalDate issueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "due_date")
    private LocalDate dueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "fine_amount")
    private Double fineAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "fine_status")
    private FineStatus fineStatus;
}
