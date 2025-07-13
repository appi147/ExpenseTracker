package com.appi147.expensetracker.model.request;

import com.appi147.expensetracker.enums.Theme;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ThemeUpdate {
    private Theme theme;
}
