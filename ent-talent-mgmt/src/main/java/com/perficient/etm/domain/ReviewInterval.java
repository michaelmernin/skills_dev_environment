package com.perficient.etm.domain;

public enum ReviewInterval {
    ANNUAL(12),
    PROJECT(6),
    INAUGURAL,
    ENGAGEMENT;

    private Integer months;

    private ReviewInterval() {
        months = null;
    }

    private ReviewInterval(int months) {
        this.months = months;
    }

    public Integer getMonths() {
        return months;
    }
}
