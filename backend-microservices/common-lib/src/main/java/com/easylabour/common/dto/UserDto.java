package com.easylabour.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @JsonProperty("id")
    private String id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("email")
    private String email;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("skills")
    private Set<String> skills;
    @JsonProperty("userType")
    private String userType; // CUSTOMER or LABOUR

    @JsonProperty("averageRating")
    private Double averageRating;
    
    @JsonProperty("reviewCount")
    private Integer reviewCount;
    
    @JsonProperty("completedBookingsCount")
    private Integer completedBookingsCount;

    // Fallback for 'role' if sent by older versions of the app
    @JsonProperty("role")
    public void setRole(String role) {
        if (this.userType == null) {
            this.userType = role;
        }
    }
}
