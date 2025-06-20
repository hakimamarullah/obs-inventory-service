package com.sg.obs.utility;

import java.time.LocalDate;

public class OrderUtil {

    public static String generateOrderNo(long sequence) {
        String datePart = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        String paddedSeq = String.format("%08d", sequence); // 8-digit zero-padded
        return datePart + paddedSeq;
    }

}
