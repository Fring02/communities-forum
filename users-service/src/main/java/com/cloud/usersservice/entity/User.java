package com.cloud.usersservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Check(constraints = "karma > 0")
@Getter
@Setter
@NoArgsConstructor
public class User {
    public User(UUID id, String firstName, String lastName, String email, String userName, int karma, List<String> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userName = userName;
        this.karma = karma;
        this.roles = roles;
    }
    public User(String firstName, String lastName, String email, String userName, int karma, List<String> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userName = userName;
        this.karma = karma;
        this.roles = roles;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "firstName", nullable = false)
    private String firstName;
    @Column(name = "lastName", nullable = false)
    private String lastName;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "userName", unique = true, nullable = false, length = 20)
    private String userName;
    @Column(name = "karma", nullable = false)
    private int karma = 1;
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "roles")
    private List<String> roles;
    @ManyToMany
    private List<User> subscribers;
    @ManyToMany(mappedBy = "subscribers", cascade = CascadeType.PERSIST)
    private List<User> subscriberOf;
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof User user)) return false;
        return user.userName.equals(this.userName) && user.email.equals(this.email);
    }
}
