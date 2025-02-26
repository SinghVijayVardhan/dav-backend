package org.dav.services;

import org.dav.entity.Book;
import org.dav.entity.Reservation;
import org.dav.entity.User;
import org.dav.enums.Authorities;
import org.dav.enums.ReservationStatus;
import org.dav.exception.BadRequestException;
import org.dav.exception.NotFoundException;
import org.dav.exception.UnAuthorizedException;
import org.dav.modals.PageResponse;
import org.dav.modals.ReservationDto;
import org.dav.repository.ReservationRepository;
import org.dav.utils.CurrentThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookService bookService;
    private final UserService userService;
    private final LoanService loanService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, BookService bookService, UserService userService, LoanService loanService) {
        this.reservationRepository = reservationRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.loanService = loanService;
    }

    public Reservation reserveBook(Integer bookId){
        Book book = bookService.getBookById(bookId);
        User user = userService.getUserById(CurrentThread.getId());
        List<Reservation> existingReservation = reservationRepository.findAllByBookAndUserAndStatus(book, user, ReservationStatus.PENDING);
        if(!existingReservation.isEmpty()){
            throw new BadRequestException("Reservation for book "+book.getTitle()+" already present");
        }
        Reservation reservation = Reservation.builder()
                .book(book)
                .user(user)
                .reservationDate(LocalDate.now())
                .build();
        return reservationRepository.save(reservation);
    }

    public Reservation updateStatus(Long reservationId, ReservationStatus status) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if(optionalReservation.isEmpty()){
            throw new NotFoundException("Invalid reservation id");
        }
        Reservation reservation = optionalReservation.get();
        if(reservation.getStatus().equals(ReservationStatus.ACCEPTED) || reservation.getStatus().equals(ReservationStatus.REJECTED) || reservation.getStatus().equals(status)){
            throw new BadRequestException("Status can not be changed for this reservation");
        }
        if(status.equals(ReservationStatus.ACCEPTED)){
            loanService.issueBook(reservation.getBook(), reservation.getUser());
        }
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id){
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if(reservation.isEmpty()){
            throw new NotFoundException("Invalid reservation");
        }
        reservationRepository.delete(reservation.get());
    }

    public PageResponse<ReservationDto> getReservations(String name, int page, int size) {
        User user = userService.getUserById(CurrentThread.getId());
        Pageable pageable = PageRequest.of(page, size);

        Page<Reservation> reservationPage;

        if (name != null) {
            if (!user.getAuthorities().equals(Authorities.librarian)) {
                throw new UnAuthorizedException("Access denied, You are not authorized for this action");
            }
            List<User> users = userService.getUsersWithProvidedName(name);
            if (users.isEmpty()) {
                return new PageResponse<>(page, size, 0, Collections.emptyList());
            }
            reservationPage = reservationRepository.findAllByUserIn(users, pageable);
        } else {
            reservationPage = reservationRepository.findAllByUser(user, pageable);
        }
        return getLoanDtoPage(reservationPage,page,size);
    }

    private PageResponse<ReservationDto> getLoanDtoPage(Page<Reservation> reservationPage, Integer page, Integer size) {
        List<Reservation> loans = reservationPage.getContent();
        List<ReservationDto> reservationDtos = loans.stream().map(ReservationDto::of).toList();
        return new PageResponse<>(page, size, reservationPage.getTotalPages(), reservationDtos);
    }
}
