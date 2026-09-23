package com.backend.warehouse.payload.request;

public class CreateUserRequest {
    private String username;
    private String password;
    private String profileName;
    private String email;
    private String role; // "ROLE_ADMIN" | "ROLE_STAFF"

    public String getUsername()              { return username; }
    public void setUsername(String u)        { this.username = u; }

    public String getPassword()              { return password; }
    public void setPassword(String p)        { this.password = p; }

    public String getProfileName()           { return profileName; }
    public void setProfileName(String n)     { this.profileName = n; }

    public String getEmail()                 { return email; }
    public void setEmail(String e)           { this.email = e; }

    public String getRole()                  { return role; }
    public void setRole(String r)            { this.role = r; }
}
