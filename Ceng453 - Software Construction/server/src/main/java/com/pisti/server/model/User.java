package com.pisti.server.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Id
    private long userId;

    @Column
    @NotNull(message = "Please write your name")
    private String name;

    @Column
    @Length(min = 6, message = "Your password must have at least 6 characters")
    @NotNull(message = "Please write your password")
    private String password;

    @Email(message = "Please write a valid Email")
    @NotNull(message = "Please write an Email")
    private String email;
}
