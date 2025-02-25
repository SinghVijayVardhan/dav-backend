package org.dav.modals;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dav.entity.User;
import org.dav.enums.Authorities;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserClaim implements UserDetails {

    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
    private Authorities authorities;
    private String token;
    private String profilePic;

    public static Map<String, String> getUserClaims(UserClaim userClaim){
        Map<String,String> claims = new HashMap<>();
        claims.put("email", userClaim.getEmail());
        claims.put("role", userClaim.getAuthorities().toString());
        claims.put("firstName", userClaim.getFirstName());
        claims.put("lastName", userClaim.getLastName());
        claims.put("id", userClaim.getUserId().toString());
        return claims;
    }

    public static UserClaim getUserClaim(User user){
        return new UserClaim(user.getId(), user.getEmail(), user.getFirstname(), user.getLastname(), user.getAuthorities(), null, user.getProfilePic());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + authorities.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
