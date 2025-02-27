package org.dav.modals;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private BookDto book;
    private ReservationStatus status;

    public static ReservationDto of(Reservation reservation){
        BookDto bookDto = BookDto.of(reservation.getBook());
        return ReservationDto.builder()
                .id(reservation.getId())
                .username(reservation.getUser().getFirstname()+" "+reservation.getUser().getLastname())
                .date(reservation.getReservationDate())
                .book(bookDto)
                .status(reservation.getStatus())
                .build();
    }
}
