package org.dav.controllers;

import org.dav.entity.Loan;
import org.dav.modals.LoanDto;
import org.dav.modals.PageResponse;
import org.dav.services.LoanService;
import org.dav.utils.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrow")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @PostMapping
    public ResponseEntity<LoanDto> issueBook(@RequestParam("email") String email,@RequestParam("bookId") Integer bookId){
        Loan loan = loanService.saveLoan(email, bookId);
        LoanDto loanDto = LoanDto.of(loan);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanDto);
    }

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @PutMapping
    public ResponseEntity<LoanDto> submitBook(@RequestBody LoanDto loan){
        Loan loan1 = loanService.submitBook(loan);
        loan = LoanDto.of(loan1);
        return ResponseEntity.ok(loan);
    }

    @GetMapping
    public ResponseEntity<PageResponse<LoanDto>> getLoans(@RequestParam(value = "name", required = false) String name,@RequestParam("page") Integer page, @RequestParam("size") Integer size){
        PageResponse<LoanDto> loans = loanService.getLoans(name,page,size);
        return ResponseEntity.ok(loans);
    }
}
