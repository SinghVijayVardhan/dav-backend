package org.dav.modals;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private List<T> data;

    public PageResponse(int currentPage, int pageSize, int totalPages, List<T> data) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.data = data;
    }
}
