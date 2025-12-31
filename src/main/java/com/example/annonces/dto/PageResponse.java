package com.example.annonces.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public class PageResponse<T> {

    private List<T> content;
    private int page;          // page courante (0-based)
    private int size;
    private long totalElements;
    private int totalPages;

    public static <T> PageResponse<T> of(Page<T> p) {
        var r = new PageResponse<T>();
        r.content = p.getContent();
        r.page = p.getNumber();
        r.size = p.getSize();
        r.totalElements = p.getTotalElements();
        r.totalPages = p.getTotalPages();
        return r;
    }

    /* =====================
       Getters Thymeleaf
       ===================== */

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    /** Alias plus clair pour la vue */
    public int getCurrentPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasPrevious() {
        return page > 0;
    }

    public boolean isHasNext() {
        return page < totalPages - 1;
    }
}
