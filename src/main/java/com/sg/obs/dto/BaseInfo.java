package com.sg.obs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sg.obs.config.constant.DatetimePattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseInfo {

    @JsonFormat(pattern = DatetimePattern.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdDate;

    @JsonFormat(pattern = DatetimePattern.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime updatedDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String updatedBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String createdBy;
}
