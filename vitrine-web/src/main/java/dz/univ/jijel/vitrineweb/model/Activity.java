package dz.univ.jijel.vitrineweb.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    private Long id;
    private String name;
    private Destination destination;
    private String description;
    private String type;
    private Double price;
    private Integer duration;
    private Double rating;
    private String imageUrl;
}