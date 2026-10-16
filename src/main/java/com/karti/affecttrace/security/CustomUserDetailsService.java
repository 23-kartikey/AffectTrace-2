package course.course_distributor.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import course.course_distributor.entity.User;
import course.course_distributor.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    
    @Autowired
    private UserRepository userRepo;

    public UserDetails loadUserByUsername(String usernameOrEmail){

        User user = userRepo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                            .orElseThrow(()->(new UsernameNotFoundException("User doesn't exist")));

        return new CustomUserDetails(user);
    }

}
