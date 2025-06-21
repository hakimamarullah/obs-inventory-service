package com.sg.obs.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderUtil {

    public static String generateOrderNo(long sequence) {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String paddedSeq = String.format("%015d", sequence); // 15-digit zero-padded
        return datePart + paddedSeq;
    }

}
