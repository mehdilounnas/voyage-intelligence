
package dz.univ.jijel.vitrineweb.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination {
    private Long id;
    private String name;
    private String country;
    private String city;
    private String description;
    private String category;
    private Double latitude;
    private Double longitude;
    private Integer popularityScore;
    private String imageUrl;
}