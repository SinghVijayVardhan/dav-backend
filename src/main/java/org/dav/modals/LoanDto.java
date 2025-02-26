package org.dav.modals;

import lombok.*;
import org.dav.entity.Loan;
import org.dav.enums.FineStatus;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class LoanDto {
    private Integer id;
    private String userName;
    private String title;
    private String author;
    private Integer publicationYear;
    private Double fine;
    private LocalDate returnDate;
    private LocalDate issueDate;
    private FineStatus fineStatus;
    private LocalDate dueDate;

    public static LoanDto of(Loan loan){
        return LoanDto.builder()
                .id(loan.getId())
                .userName(loan.getUser().getFirstname() + " " + loan.getUser().getLastname())
                .title(loan.getBook().getTitle())
                .author(loan.getBook().getAuthor())
                .publicationYear(loan.getBook().getPublicationYear())
                .fine(loan.getFineAmount())
                .returnDate(loan.getReturnDate())
                .issueDate(loan.getIssueDate())
                .fineStatus(loan.getFineStatus())
                .dueDate(loan.getDueDate())
                .build();
    }
}
