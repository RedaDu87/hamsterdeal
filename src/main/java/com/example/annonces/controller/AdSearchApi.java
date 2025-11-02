package com.example.annonces.controller;

import com.example.annonces.domain.Ad;
import com.example.annonces.service.AdvancedSearchService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ads")
public class AdSearchApi {

    private final AdvancedSearchService search;

    public AdSearchApi(AdvancedSearchService search){ this.search = search; }

    @PostMapping("/search")
    public PageResult<Ad> search(@RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(defaultValue = "12") Integer size,
                                 @RequestBody Map<String, Object> filters){
        Page<Ad> p = search.search(page, size, filters);
        return PageResult.of(p);
    }

    public record PageResult<T>(java.util.List<T> items, int page, int size, long total, int totalPages){
        static <T> PageResult<T> of(Page<T> p){
            return new PageResult<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        }
    }
}
