package com.tecvify.springsecuriitytutorial.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String matchingPassword;

}
