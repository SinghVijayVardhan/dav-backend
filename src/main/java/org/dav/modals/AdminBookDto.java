package org.dav.modals;

import lombok.*;
import org.dav.entity.Book;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminBookDto extends BookDto{
    private Integer availableCopies;
    private Integer totalCopies;

    public static AdminBookDto of(Book book){
        AdminBookDto bookDto = new AdminBookDto();
        bookDto.setAvailableCopies(book.getAvailableCopies());
        bookDto.setTotalCopies(book.getTotalCopies());
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPublicationYear(book.getPublicationYear());
        bookDto.setCategory(book.getCategory());
        return bookDto;
    }

    public String getType(){
        return "librarian";
    }
}
