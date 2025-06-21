package com.sg.obs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sg.obs.config.constant.DatetimePattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BaseInfo {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = DatetimePattern.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime createdDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = DatetimePattern.YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime updatedDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String updatedBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String createdBy;
}
