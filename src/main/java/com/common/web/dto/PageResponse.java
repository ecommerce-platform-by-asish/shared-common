package com.common.web.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/** Standard wrapper for paginated API responses. */
public record PageResponse<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean isFirst,
    boolean isLast,
    boolean hasNext,
    boolean hasPrevious) {

  public static <T> PageResponse<T> of(Page<T> page) {
    return new PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast(),
        page.hasNext(),
        page.hasPrevious());
  }
}
