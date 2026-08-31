package com.vanillacakes;

import java.util.List;

/**
 * Represents a paginated database query result.
 * @author italo617
 */
public record PagedResult<T>(List<T> content, int page, int pageSize, long totalElements, int totalPages) {
    public PagedResult {
        if (content == null) {
            throw new IllegalArgumentException("content cannot be null.");
        }
        //For this project, 1-indexed is the default.
        if (page < 1) {
            throw new IllegalArgumentException("page must be a positive number.");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be a positive number.");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("totalElements must be a non-negative number.");
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("totalPages must be a non-negative number.");
        }
        if (content.size() > pageSize) {
            throw new IllegalArgumentException("content size cannot exceed pageSize.");
        }
    }
}
