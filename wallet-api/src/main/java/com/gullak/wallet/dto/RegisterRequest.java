package com.gullak.wallet.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotNull @Size(min = 3, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9_.]+$")
    private String username;

    @NotNull @Size(min = 2, max = 50)
    private String fullName;

    @NotNull @Size(min = 6, max = 72)
    private String password;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 250)
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must contain a valid domain extension"
    )
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
