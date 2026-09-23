package com.backend.warehouse.payload.request;

public class UpdateUserRequest {
    private String profileName;
    private String email;
    private String role; // "ROLE_ADMIN" | "ROLE_STAFF"

    public String getProfileName()           { return profileName; }
    public void setProfileName(String n)     { this.profileName = n; }

    public String getEmail()                 { return email; }
    public void setEmail(String e)           { this.email = e; }

    public String getRole()                  { return role; }
    public void setRole(String r)            { this.role = r; }
}
