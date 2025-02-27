package org.dav.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dav.entity.Book;
import org.dav.entity.Loan;
import org.dav.entity.User;
import org.dav.enums.Authorities;
import org.dav.enums.FineStatus;
import org.dav.exception.BadRequestException;
import org.dav.exception.InternalServerException;
import org.dav.exception.NotFoundException;
import org.dav.exception.UnAuthorizedException;
import org.dav.modals.LibraryBookConfig;
import org.dav.modals.LoanDto;
import org.dav.modals.PageResponse;
import org.dav.repository.LoanRepository;
import org.dav.utils.ConfigurationKey;
import org.dav.utils.CurrentThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final ConfigurationService configurationService;
    private final ObjectMapper objectMapper;
    private final BookService bookService;
    private final UserService userService;

    public LoanService(LoanRepository loanRepository, ConfigurationService configurationService, ObjectMapper objectMapper, BookService bookService, UserService userService) {
        this.loanRepository = loanRepository;
        this.configurationService = configurationService;
        this.objectMapper = objectMapper;
        this.bookService = bookService;
        this.userService = userService;
    }

    @Transactional
    public Loan issueBook(Book book, User user) {
        List<Loan> loans = loanRepository.findAllByUserAndReturnDate(user, null);
        JsonNode jsonNode = configurationService.getConfigurationByType(ConfigurationKey.LIBRARY_ISSUE_CONFIG);
        LibraryBookConfig config = null;
        try {
            config = objectMapper.readValue(jsonNode.traverse(),new TypeReference<LibraryBookConfig>(){});
        } catch (IOException e) {
            throw new InternalServerException("Some exception in configuration");
        }
        if(loans.size() > config.getMaxNumberOfBooks()){
            throw new BadRequestException("You have reached max number of books each user is allowed");
        }
        Optional<Loan> existingLoanForSameBook = loans.stream().filter(loan -> Objects.equals(loan.getBook().getId(), book.getId())).findFirst();
        if(existingLoanForSameBook.isPresent()){
            throw new BadRequestException("Same book can't be borrowed twice");
        }
        if(book.getAvailableCopies()>0){
            book.setAvailableCopies(book.getAvailableCopies()-1);
            bookService.saveOrUpdate(book);
        }else{
            throw new BadRequestException("This book is currently not available");
        }
        Loan loan = Loan.builder()
                .book(book)
                .user(user)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(config.getMaxNumberOfDays()))
                .fineStatus(FineStatus.none)
                .build();
        loanRepository.save(loan);
        return loanRepository.save(loan);
    }

    public Loan saveLoan(String email, Integer bookId){
        Book book = bookService.getBookById(bookId);
        User user = userService.getUserByEmail(email);
        return issueBook(book, user);
    }

    @Transactional
    public Loan submitBook(LoanDto loanDto){
        Optional<Loan> existingLoan = loanRepository.findById(loanDto.getId());
        if(existingLoan.isEmpty())
            throw new NotFoundException("Invalid borrow id : "+loanDto.getId());
        Loan loan = existingLoan.get();
        calculateFine(loan);
        if(!loanDto.getFine().equals(loan.getFineAmount()))
            throw new BadRequestException("Fine amount mismatch");
        if(loan.getFineAmount()==0 && !loanDto.getFineStatus().equals(FineStatus.none) && loanDto.getFine()>0)
            throw new BadRequestException("This user has no fine");
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies()+1);
        bookService.saveOrUpdate(book);
        loan.setFineStatus(loanDto.getFineStatus());
        loan.setReturnDate(LocalDate.now());
        return loanRepository.save(loan);
    }

    public PageResponse<LoanDto> getLoans(String name, int page, int size) {
        User user = userService.getUserById(CurrentThread.getId());
        Pageable pageable = PageRequest.of(page, size);

        Page<Loan> loanPage;

        if (name != null && !name.isBlank()) {
            if (!user.getAuthorities().equals(Authorities.librarian)) {
                throw new UnAuthorizedException("Access denied, You are not authorized for this action");
            }
            List<User> users = userService.getUsersWithProvidedName(name);
            if (users.isEmpty()) {
                return new PageResponse<>(page, size, 0, Collections.emptyList());
            }
            loanPage = loanRepository.findAllByUserIn(users, pageable);
        } else {
            loanPage = loanRepository.findAllByUser(user, pageable);
        }
        return getLoanDtoPage(loanPage,page,size);
    }

    private PageResponse<LoanDto> getLoanDtoPage(Page<Loan> loanPage, Integer page, Integer size) {
        List<Loan> loans = Optional.of(loanPage.getContent())
                .orElse(Collections.emptyList());
        loans.forEach(this::calculateFine);
        List<LoanDto> loanDtos = loans.stream().map(LoanDto::of).toList();
        return new PageResponse<>(page, size, loanPage.getTotalPages(), loanDtos);
    }

    private void calculateFine(Loan loan){
        LibraryBookConfig config = configurationService.getLibraryConfiguration();
        Long expectedDays = ChronoUnit.DAYS.between( loan.getDueDate(), loan.getIssueDate());
        Long daysTillToday = ChronoUnit.DAYS.between(LocalDate.now(), loan.getIssueDate());
        double fine = (daysTillToday - expectedDays) * config.getFinePerDay();
        if(fine==0){
            loan.setFineStatus(FineStatus.none);
        }
        loan.setFineAmount(fine);
    }
}
