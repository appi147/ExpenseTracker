package com.appi147.expensetracker.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AmortizationPeriod {
    ONE_MONTH(1),
    TWO_MONTH(2),
    THREE_MONTH(3),
    SIX_MONTH(6),
    TWELVE_MONTH(12);

    private final int months;

    @JsonCreator
    public static AmortizationPeriod fromValue(int value) {
        for (AmortizationPeriod period : values()) {
            if (period.getMonths() == value) {
                return period;
            }
        }
        throw new IllegalArgumentException("Invalid months value: " + value);
    }

    @JsonValue
    public int toValue() {
        return this.months;
    }
}
