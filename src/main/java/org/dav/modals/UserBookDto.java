package org.dav.modals;

import lombok.*;
import org.dav.entity.Book;
import org.dav.enums.UserBookStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserBookDto extends BookDto{
    private UserBookStatus status;

    public static UserBookDto of(Book book){
        UserBookDto bookDto = new UserBookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPublicationYear(book.getPublicationYear());
        bookDto.setCategory(book.getCategory());
        UserBookStatus userBookStatus = book.getAvailableCopies() > 0 ? UserBookStatus.AVAILABLE : UserBookStatus.UNAVAILABLE;
        bookDto.setStatus(userBookStatus);
        return bookDto;
    }

    @Override
    public String getType(){
        return "member";
    }
}
