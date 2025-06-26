package com.lreas.forum.dtos;

import org.springframework.data.domain.Page;

public class PaginationInfoDto<T> {
    public int page;
    public int pageSize;
    public int totalPages;

    public PaginationInfoDto() {}

    public PaginationInfoDto(Page<T> page) {
        this.page = page.getNumber();
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
    }
}
