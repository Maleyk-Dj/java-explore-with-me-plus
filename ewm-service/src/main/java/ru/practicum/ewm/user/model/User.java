package ru.practicum.ewm.user.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_email", columnNames = "email")
)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 254)       // уникален email
    private String email;

    @Column(nullable = false, length = 250)       // без unique
    private String name;
}

