package com.gullak.wallet.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class RegisterRequest {

    @NotNull
    private String username;
    @NotNull
    private String fullName;
    @NotNull
    private String password;
    @NotNull @Email
    private String email;

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getFullName(){
        return fullName;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }


}
