package com.appi147.expensetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "role")
    private String role = "user";

    @Column(name = "last_login")
    private ZonedDateTime lastLogin;

}
