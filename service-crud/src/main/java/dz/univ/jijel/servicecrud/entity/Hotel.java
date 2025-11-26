
package dz.univ.jijel.servicecrud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom de l'hôtel est obligatoire")
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;
    
    @NotBlank
    private String address;
    
    @Min(1)
    @Max(5)
    private Integer stars; // Nombre d'étoiles (1-5)
    
    @DecimalMin("0.0")
    private Double pricePerNight;
    
    @Column(length = 1000)
    private String amenities; // WiFi, Piscine, Restaurant, etc.
    
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double rating; // Note moyenne (0-5)
    
    private String imageUrl;
}

// ====================================