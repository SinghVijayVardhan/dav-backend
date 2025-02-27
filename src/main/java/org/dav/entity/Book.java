package org.dav.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "author")
    private String author;
    @Column(name = "publication_year")
    private Integer publicationYear;
    @Column(name = "total_copies")
    private Integer totalCopies = 1;
    @Column(name = "remaining_copies")
    private Integer availableCopies;
    @Column(name = "category")
    private String category;
}
