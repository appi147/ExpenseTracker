package com.appi147.expensetracker.auth;

import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomUserDetailsTest {

    @Test
    void getAuthorities_returnsRoleWithPrefix() {
        User user = mock(User.class);
        when(user.getRole()).thenReturn(Role.SUPER_USER);
        CustomUserDetails cud = new CustomUserDetails(user);

        List<? extends GrantedAuthority> auths = List.copyOf(cud.getAuthorities());
        assertEquals(1, auths.size());
        assertTrue(auths.getFirst() instanceof SimpleGrantedAuthority);
        assertEquals("ROLE_SUPER_USER", auths.getFirst().getAuthority());
    }

    @Test
    void getPassword_returnsNull() {
        User user = mock(User.class);
        CustomUserDetails cud = new CustomUserDetails(user);
        assertNull(cud.getPassword());
    }

    @Test
    void getUsername_returnsUserId() {
        User user = mock(User.class);
        when(user.getUserId()).thenReturn("johndoe");
        CustomUserDetails cud = new CustomUserDetails(user);
        assertEquals("johndoe", cud.getUsername());
    }

    @Test
    void accountFlags_allTrue() {
        CustomUserDetails cud = new CustomUserDetails(mock(User.class));
        assertTrue(cud.isAccountNonExpired());
        assertTrue(cud.isAccountNonLocked());
        assertTrue(cud.isCredentialsNonExpired());
        assertTrue(cud.isEnabled());
    }

    @Test
    void constructor_savesReference() {
        User user = mock(User.class);
        CustomUserDetails cud = new CustomUserDetails(user);
        assertSame(user, cud.user());
    }
}
