package com.application.booking.dto;

import lombok.Data;

import java.util.List;

@Data
public class PagedResponseDto<T> {
    private List<T> data;
    private int totalPages;
    private long totalRecords;
}
