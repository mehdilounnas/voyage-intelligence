
package dz.univ.jijel.servicecrud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom de l'activité est obligatoire")
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;
    
    @Column(length = 2000)
    private String description;
    
    private String type; // visite, sport, culture, gastronomie, etc.
    
    @DecimalMin("0.0")
    private Double price;
    
    @Min(1)
    private Integer duration; // Durée en heures
    
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    private Double rating;
    
    private String imageUrl;
}