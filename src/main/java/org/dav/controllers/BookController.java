package org.dav.controllers;

import org.dav.modals.AdminBookDto;
import org.dav.modals.BookDto;
import org.dav.modals.PageResponse;
import org.dav.services.BookService;
import org.dav.utils.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResponse<? extends BookDto> getBook(@RequestParam(value = "title", required = false) String name,@RequestParam("page") Integer page, @RequestParam("size") Integer size, @RequestParam(value = "category", required = false) String category){
        return bookService.getPaginatedBookResponse(name,page,size,category);
    }

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @PostMapping
    public ResponseEntity<String> saveBooks(@RequestBody List<AdminBookDto> bookDtos) {
        bookService.saveBooks(bookDtos);
        return ResponseEntity.ok("Books saved successfully");
    }

    @Secured({ConfigurationKey.ROLE_LIBRARIAN})
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(){
        List<String> categories = bookService.getCategories();
        return ResponseEntity.ok(categories);
    }
}
