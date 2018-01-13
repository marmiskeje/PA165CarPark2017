package cz.muni.fi.pa165.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for auth user
 */
public class UserAuthDTO {

    @JsonProperty("username")
    private String userName;

    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
