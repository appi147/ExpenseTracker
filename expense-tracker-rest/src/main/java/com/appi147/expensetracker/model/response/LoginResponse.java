package com.appi147.expensetracker.model.response;

import com.appi147.expensetracker.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoginResponse {

    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String pictureUrl;

    public LoginResponse(User user) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.fullName = user.getFullName();
        this.pictureUrl = user.getPictureUrl();
    }
}
