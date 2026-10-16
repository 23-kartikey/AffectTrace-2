package com.karti.affecttrace.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.management.relation.Role;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Table(name="Users")
public class User{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String firstName;

    @Column(length = 20)
    private String lastName;

    @Column(unique=true, length = 40, updatable = true)
    private String username;

    @Column(nullable = false, unique=true, length=30)
    private String email;

    @Column(nullable = false, length = 200)
    private String password;

    @Column( length = 300)
    private String about;

    @Column
    private String profilePictureUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="user_roles",
            joinColumns= @JoinColumn(name="user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="role_id", referencedColumnName="id")
    )
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="follow_table",
            joinColumns = @JoinColumn(name="follower_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="following_id", referencedColumnName = "id")
    )
    private Set<User> following;

    @ManyToMany(mappedBy = "following")
    private Set<User> followers;

}

