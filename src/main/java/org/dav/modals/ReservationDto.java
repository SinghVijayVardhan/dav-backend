package org.dav.modals;

import lombok.*;
import org.dav.entity.Reservation;
import org.dav.enums.ReservationStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    private Long id;
    private String username;
    private LocalDate date;
    private BookDto book;
    private ReservationStatus status;

    public static ReservationDto of(Reservation reservation){
        BookDto bookDto = BookDto.builder()
                .id(reservation.getBook().getId())
                .title(reservation.getBook().getTitle())
                .author(reservation.getBook().getAuthor())
                .publicationYear(reservation.getBook().getPublicationYear())
                .build();
        return ReservationDto.builder()
                .id(reservation.getId())
                .username(reservation.getUser().getFirstname()+" "+reservation.getUser().getLastname())
                .date(reservation.getReservationDate())
                .book(bookDto)
                .status(reservation.getStatus())
                .build();
    }
}
