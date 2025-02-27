package org.dav.modals;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.dav.entity.Book;


@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "librarian", value = AdminBookDto.class),
        @JsonSubTypes.Type(name = "member", value = UserBookDto.class),
        @JsonSubTypes.Type(name = "default", value = BookDto.class)
})
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BookDto {
    private Integer id;
    private String title;
    private String author;
    private String category;
    private Integer publicationYear;

    public static BookDto of(Book book){
        return BookDto.builder()
                .author(book.getAuthor())
                .id(book.getId())
                .publicationYear(book.getPublicationYear())
                .title(book.getTitle())
                .category(book.getCategory())
                .build();
    }

    public String getType(){
        return "default";
    }
}
