package com.appi147.expensetracker.model;

import com.appi147.expensetracker.model.response.LoginResponse;

public record TimedLoginResponse(LoginResponse response, long expiryEpochSeconds) {
}
