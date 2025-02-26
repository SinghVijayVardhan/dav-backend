package org.dav.services;

import lombok.extern.slf4j.Slf4j;
import org.dav.entity.User;
import org.dav.exception.UserNotFoundException;
import org.dav.modals.UserClaim;
import org.dav.repository.UserRepository;
import org.dav.utils.CurrentThread;
import org.dav.utils.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
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
       return UserClaim.getUserClaim(userRepository.findUserByEmail(username).orElseThrow(()->new UserNotFoundException("User not found")));
    }

    public User getUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }else{
            throw new UserNotFoundException("User not found");
        }
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public UserClaim login(String idToken){
        User user = authService.googleSignIn(idToken);
        try {
            user = getUserByEmail(user.getEmail());
        }catch(UserNotFoundException exception){
            log.info("Creating user with email : {}",user.getEmail());
            user = save(user);
        }finally {
            CurrentThread.setEmail(user.getEmail());
            CurrentThread.setId(user.getId());
        }
        UserClaim userClaim = UserClaim.getUserClaim(user);
        userClaim.setToken(jwtService.generateToken(userClaim));
        return userClaim;
    }

}
