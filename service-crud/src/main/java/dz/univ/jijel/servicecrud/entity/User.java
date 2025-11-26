package dz.univ.jijel.servicecrud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50)
    private String firstName;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50)
    private String lastName;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(unique = true)
    private String email;
    
    @Column(unique = true, length = 50)
    private String username;
    
    @Column(length = 255)
    private String password; // Will be synced from Keycloak
    
    @Column(length = 20)
    private String role; // USER, ADMIN
    
    private Boolean enabled = true;
    
    @NotBlank(message = "Le pays est obligatoire")
    private String country;
    
    @Column(length = 1000)
    private String preferences;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (enabled == null) enabled = true;
        if (role == null) role = "USER";
    }
}
