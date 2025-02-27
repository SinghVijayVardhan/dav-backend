package org.dav.services;

import org.dav.entity.Book;
import org.dav.entity.User;
import org.dav.enums.Authorities;
import org.dav.enums.ReservationStatus;
import org.dav.enums.UserBookStatus;
import org.dav.exception.NotFoundException;
import org.dav.modals.AdminBookDto;
import org.dav.modals.BookDto;
import org.dav.modals.PageResponse;
import org.dav.modals.UserBookDto;
import org.dav.repository.BookRepository;
import org.dav.utils.CurrentThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserService userService;

    @Autowired
    public BookService(BookRepository bookRepository, UserService userService) {
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    public Book getBookById(Integer id){
        Optional<Book> book = bookRepository.findById(id);
        if(book.isEmpty()){
            throw new NotFoundException("Book not available");
        }
        return book.get();
    }

    @Transactional
    public void saveOrUpdate(Book book) {
        Optional<Book> existingBook = bookRepository.findById(book.getId());
        if(existingBook.isEmpty())
            throw new NotFoundException("Book not available");
        bookRepository.save(book);
    }

    public PageResponse<? extends BookDto> getPaginatedBookResponse(String name, Integer page, Integer size,String category) {
        User user = null;
        if(CurrentThread.getId()!=null)
            user = userService.getUserById(CurrentThread.getId());
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books;
        if(name!=null && !name.isBlank()){
            books = category!=null ? bookRepository.findAllTitleAndCategoryContaining("%"+name+"%", "%"+category+"%", pageable) : bookRepository.findAllTitleContaining("%"+name+"%", pageable);
        }else{
            books = category!=null ? bookRepository.findAllCategoryContaining("%"+category+"%", pageable)  : bookRepository.findAll(pageable);
        }
        return getBookDtos(user, books, page, size);
    }

    public PageResponse<? extends BookDto> getBookDtos(User user, Page<Book> books, int page, int size) {
        List<? extends BookDto> bookDtos;
        if (user == null) {
            bookDtos = books.getContent().stream().map(BookDto::of).toList();
        } else if (user.getAuthorities().equals(Authorities.librarian)) {
            bookDtos = books.getContent().stream().map(AdminBookDto::of).toList();
        } else {
            List<Book> issuedBooks = getBooksIssuedByUser(user);
            List<Book> reservedBooks = getBooksReservedByUser(user);
            List<UserBookDto> userBookDtos = books.getContent().stream().map(UserBookDto::of).toList();
            for(Book book : issuedBooks){
                for(UserBookDto bookDto : userBookDtos){
                   if(book.getId().equals(bookDto.getId())){
                       bookDto.setStatus(UserBookStatus.ISSUED);
                   }
                }
            }
            for(Book book : reservedBooks){
                for (UserBookDto bookDto : userBookDtos){
                    if(book.getId().equals(bookDto.getId())){
                        bookDto.setStatus(UserBookStatus.REGISTERED);
                    }
                }
            }
            bookDtos = userBookDtos;
        }
        return new PageResponse<>(page, size, books.getTotalPages(), bookDtos);
    }

    private List<Book> getBooksReservedByUser(User user) {
        return bookRepository.findAllBookReservedByUserAndStatus(user.getId(), List.of(ReservationStatus.PENDING.name(), ReservationStatus.REJECTED.name()));
    }

    private List<Book> getBooksIssuedByUser(User user) {
        return bookRepository.findAllBookIssuedByUser(user.getId());
    }

    @Transactional
    public void saveBooks(List<AdminBookDto> bookDtos) {
        if (bookDtos.isEmpty()) {
            return;
        }

        List<String> titles = bookDtos.stream().map(BookDto::getTitle).distinct().toList();
        List<Integer> years = bookDtos.stream().map(BookDto::getPublicationYear).distinct().toList();

        List<Book> existingBooks = bookRepository.findByTitleInAndPublicationYearIn(titles, years);

        Map<String, Book> bookMap = existingBooks.stream()
                .collect(Collectors.toMap(book -> book.getTitle() + "-" + book.getPublicationYear(), book -> book));

        List<Book> booksToSave = new ArrayList<>();

        for (AdminBookDto dto : bookDtos) {
            String key = dto.getTitle() + "-" + dto.getPublicationYear();
            if (bookMap.containsKey(key)) {
                Book existingBook = bookMap.get(key);
                existingBook.setTotalCopies(existingBook.getTotalCopies() + dto.getTotalCopies());
                existingBook.setAvailableCopies(existingBook.getAvailableCopies() + dto.getAvailableCopies());
            } else {
                Book newBook = new Book();
                newBook.setTitle(dto.getTitle());
                newBook.setAuthor(dto.getAuthor());
                newBook.setPublicationYear(dto.getPublicationYear());
                newBook.setTotalCopies(dto.getTotalCopies());
                newBook.setAvailableCopies(dto.getAvailableCopies());
                booksToSave.add(newBook);
            }
        }

        bookRepository.saveAll(existingBooks);
        bookRepository.saveAll(booksToSave);
    }

    @Transactional
    public void deleteBook(Integer bookId) {
        if (bookId == null || !bookRepository.existsById(bookId)) {
            throw new NotFoundException("Book with ID " + bookId + " not found.");
        }
        bookRepository.deleteById(bookId);
    }

    public List<String> getCategories(){
        return bookRepository.findAllCategories();
    }
}
