package com.tecvify.springsecuriitytutorial.controller;

import com.tecvify.springsecuriitytutorial.entity.User;
import com.tecvify.springsecuriitytutorial.entity.VerificationToken;
import com.tecvify.springsecuriitytutorial.event.RegistrationCompleteEvent;
import com.tecvify.springsecuriitytutorial.model.PasswordModel;
import com.tecvify.springsecuriitytutorial.model.UserModel;
import com.tecvify.springsecuriitytutorial.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    private final ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User verify successfully";
        }
        return "Bad user";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest http) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        resendVerificationTokenMail(applicationUrl(http), verificationToken);
        return "Verification link resent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if (user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(applicationUrl(request), token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid token";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password reset successfully";
        }
        return "Invalid token";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordModel.getOldPassword())) {
            return "Old password invalid";
        }
        userService.changePassword(user, passwordModel.getNewPassword());
        return "Change password successfully";
    }

    private String passwordResetTokenMail(String applicationUrl, String token) {
        String url = applicationUrl + "/savePassword?token=" + token;
        log.info("Click the link to reset password: " + url);
        return url;
    }

    private void resendVerificationTokenMail(String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl + "/verifyRegistration?token=" + verificationToken.getToken();
        log.info("Click the link to verify your account: " + url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
