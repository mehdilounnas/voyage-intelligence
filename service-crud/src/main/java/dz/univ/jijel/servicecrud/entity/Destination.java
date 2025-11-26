
package dz.univ.jijel.servicecrud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "destinations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom de la destination est obligatoire")
    private String name;
    
    @NotBlank(message = "Le pays est obligatoire")
    private String country;
    
    @NotBlank(message = "La ville est obligatoire")
    private String city;
    
    @Column(length = 2000)
    private String description;
    
    private String category; // plage, montagne, culture, aventure, etc.
    
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;
    
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;
    
    @Min(0)
    private Integer popularityScore; // Score de popularité (0-100)
    
    private String imageUrl;
}