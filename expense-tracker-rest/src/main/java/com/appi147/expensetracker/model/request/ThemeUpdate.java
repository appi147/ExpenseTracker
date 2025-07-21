package com.appi147.expensetracker.model.request;

import com.appi147.expensetracker.enums.Theme;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ThemeUpdate {
    @NotNull(message = "Theme must not be null")
    private Theme theme;
}
