package org.dav.services;

import org.dav.entity.User;
import org.dav.modals.UserClaim;
import org.dav.repository.UserRepository;
import org.dav.utils.CurrentThread;
import org.dav.utils.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final GoogleAuthService authService;

    @Autowired
    public UserService(UserRepository userRepository, JwtService jwtService, GoogleAuthService authService){
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    public UserDetails loadUserByUsername(String username) {
       return UserClaim.getUserClaim(userRepository.findUserByEmail(username).orElseThrow(()->new RuntimeException("User not found")));
    }

    public User getUserByEmail(String idToken) {
        User user = authService.googleSignIn(idToken);
        Optional<User> optionalUser = userRepository.findUserByEmail(user.getEmail());
        if(optionalUser.isEmpty()){
            user = userRepository.save(user);
        }else{
            user = optionalUser.get();
        }
        CurrentThread.setEmail(user.getEmail());
        CurrentThread.setId(user.getId());
        return user;
    }

    public UserClaim login(String idToken){
        User user = getUserByEmail(idToken);
        UserClaim userClaim = UserClaim.getUserClaim(user);
        userClaim.setToken(jwtService.generateToken(userClaim));
        return userClaim;
    }

}
