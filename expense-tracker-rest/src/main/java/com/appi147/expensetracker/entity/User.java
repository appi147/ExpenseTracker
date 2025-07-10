package com.appi147.expensetracker.entity;

import com.appi147.expensetracker.enums.Role;
import com.appi147.expensetracker.enums.Theme;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User extends Auditable {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role = Role.USER;

    @Column(name = "last_login")
    private ZonedDateTime lastLogin;

    @Column(name = "budget", precision = 19, scale = 2)
    private BigDecimal budget = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_theme", nullable = false, length = 10)
    private Theme preferredTheme = Theme.SYSTEM;

}
