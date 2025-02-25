package org.dav.entity;

import jakarta.persistence.*;
import lombok.*;
import org.dav.enums.Authorities;

@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "email")
    private String email;
    @Column(name = "firstname")
    private String firstname;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Authorities authorities = Authorities.member;
    @Column(name = "profile_pic")
    private String profilePic;
}
