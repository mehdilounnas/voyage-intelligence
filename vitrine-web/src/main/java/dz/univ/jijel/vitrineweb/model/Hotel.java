package dz.univ.jijel.vitrineweb.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {
    private Long id;
    private String name;
    private Destination destination;
    private String address;
    private Integer stars;
    private Double pricePerNight;
    private String amenities;
    private Double rating;
    private String imageUrl;
}
