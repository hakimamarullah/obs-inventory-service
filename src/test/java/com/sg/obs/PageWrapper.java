package com.sg.obs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageWrapper<T> {

    private List<T> content;

    private PageMetadata page;

    public PageWrapper(PagedModel<T> pagedModel) {
        this.content = pagedModel.getContent();
        this.page = new PageMetadata();
        PagedModel.PageMetadata metadata = pagedModel.getMetadata();
        this.page.setNumber(Optional.ofNullable(metadata).map(PagedModel.PageMetadata::number).orElse(0L));
        this.page.setSize(Optional.ofNullable(metadata).map(PagedModel.PageMetadata::size).orElse(0L));
        this.page.setTotalElements(Optional.ofNullable(metadata).map(PagedModel.PageMetadata::totalElements).orElse(0L));
        this.page.setTotalPages(Optional.ofNullable(metadata).map(PagedModel.PageMetadata::totalPages).orElse(0L));
    }

    @Setter
    @Getter
    static class PageMetadata {
        private long number;
        private long size;
        private long totalElements;
        private long totalPages;
    }
}
