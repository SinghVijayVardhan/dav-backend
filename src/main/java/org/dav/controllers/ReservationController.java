package org.dav.controllers;

import org.dav.entity.Reservation;
import org.dav.enums.ReservationStatus;
import org.dav.modals.BookDto;
import org.dav.modals.PageResponse;
import org.dav.modals.ReservationDto;
import org.dav.services.ReservationService;
import org.dav.utils.ConfigurationKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<ReservationDto> saveReservation(@PathVariable("id") Integer bookId){
        Reservation reservation = reservationService.reserveBook(bookId);
        ReservationDto reservationDto = ReservationDto.of(reservation);
        return ResponseEntity.ok(reservationDto);
    }

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @PutMapping
    public ResponseEntity<ReservationDto> updateStatus(@RequestParam("id") Long id, @RequestParam("status") ReservationStatus status) {
        Reservation reservation = reservationService.updateStatus(id,status);
        ReservationDto reservationDto = ReservationDto.of(reservation);
        return ResponseEntity.ok(reservationDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unReserve(@PathVariable("id") Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<ReservationDto>> getReservations(@RequestParam(value = "name", required = false) String name, @RequestParam("page") Integer page, @RequestParam("size") Integer size){
        PageResponse<ReservationDto> reservations = reservationService.getReservations(name,page,size);
        return ResponseEntity.ok(reservations);
    }

}
