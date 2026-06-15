package com.bookdemo11.booking.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPreview {

    private int totalAmount;
    private int discountAmount;
    private int actualAmount;
    private int roomCount;
    private long nights;
    private List<PreviewLine> lines = new ArrayList<>();

    @Getter
    @Setter
    public static class PreviewLine {
        private String name;
        private String description;
        private long unitAmount;
        private long quantity;
    }
}