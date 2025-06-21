package com.sg.obs.utility;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class OrderUtilTest {

    @Test
    void givenOrderSequence_whenGenerateOrderNumber_thenShouldReturnCorrectFormat() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String paddedSeq = String.format("%015d", 111); // 15-digit zero-padded

        // Assert
        String expectedOrderNo = datePart + paddedSeq;
        String actualOrderNo = OrderUtil.generateOrderNo(111L);

        // Assert
        assertThat(actualOrderNo).isEqualTo(expectedOrderNo);
        assertThat(actualOrderNo.length()).isEqualTo(21);
    }
}