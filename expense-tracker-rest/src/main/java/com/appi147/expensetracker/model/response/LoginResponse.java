package com.appi147.expensetracker.model.response;

import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.enums.Role;
import com.appi147.expensetracker.enums.Theme;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
public class LoginResponse {

    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String pictureUrl;
    private Role role;
    private BigDecimal budget;
    private Theme preferredTheme;

    @JsonIgnore
    private User user;

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
