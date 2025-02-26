package org.dav.modals;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LibraryBookConfig {
    private Double finePerDay;
    private Integer maxNumberOfDays;
    private Integer maxNumberOfBooks;
}
