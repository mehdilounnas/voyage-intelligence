package dz.univ.jijel.vitrineweb.model;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IARecommendation {
    private String destination;
    private String recommendation;
    private Map<String, Object> weather;
}