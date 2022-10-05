package com.tecvify.springsecuriitytutorial.event.listener;

import com.tecvify.springsecuriitytutorial.entity.User;
import com.tecvify.springsecuriitytutorial.event.RegistrationCompleteEvent;
import com.tecvify.springsecuriitytutorial.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //Create the Verification Token for the User with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token, user);
        //send Mail to User
        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;
        //send verification email
        log.info("Click the link to verify your account: " + url);
    }
}
