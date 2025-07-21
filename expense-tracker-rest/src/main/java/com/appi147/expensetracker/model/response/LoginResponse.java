package com.appi147.expensetracker.model.response;

import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.enums.Role;
import com.appi147.expensetracker.enums.Theme;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class LoginResponse {

    private final String email;
    private final String firstName;
    private final String lastName;
    private final String fullName;
    private final String pictureUrl;
    private final Role role;
    private final BigDecimal budget;
    private final Theme preferredTheme;

    @JsonIgnore
    private final User user;

    public LoginResponse(User user) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFullName();
        this.pictureUrl = user.getPictureUrl();
        this.role = user.getRole();
        this.budget = user.getBudget();
        this.preferredTheme = user.getPreferredTheme();
        this.user = user;
    }
}
