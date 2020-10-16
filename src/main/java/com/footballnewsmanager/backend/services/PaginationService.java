package com.footballnewsmanager.backend.services;

import com.footballnewsmanager.backend.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;

public class PaginationService {
    public static <T> void handlePaginationErrors(int page, Page<T> pages) {
        if (page + 1 > pages.getTotalPages())
            throw new ResourceNotFoundException("Nie ma już więcej wyników");
        if (pages.getTotalElements() == 0)
            throw new ResourceNotFoundException("Dla podanej frazy nie ma żadnej drużyny");
    }
}
