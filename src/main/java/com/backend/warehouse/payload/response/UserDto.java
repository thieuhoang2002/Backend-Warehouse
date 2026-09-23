package com.backend.warehouse.payload.response;

public class UserDto {
    private long userId;
    private String username;
    private String profileName;
    private String email;
    private String role;

    public UserDto() {}

    public UserDto(long userId, String username, String profileName, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.profileName = profileName;
        this.email = email;
        this.role = role;
    }

    public long getUserId()              { return userId; }
    public void setUserId(long userId)   { this.userId = userId; }

    public String getUsername()                  { return username; }
    public void setUsername(String username)     { this.username = username; }

    public String getProfileName()               { return profileName; }
    public void setProfileName(String name)      { this.profileName = name; }

    public String getEmail()                     { return email; }
    public void setEmail(String email)           { this.email = email; }

    public String getRole()                      { return role; }
    public void setRole(String role)             { this.role = role; }
}
