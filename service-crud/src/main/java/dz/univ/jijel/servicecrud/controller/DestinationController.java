
package dz.univ.jijel.servicecrud.controller;

import dz.univ.jijel.servicecrud.entity.Destination;
import dz.univ.jijel.servicecrud.repository.DestinationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/destinations")
@CrossOrigin(origins = "http://localhost:8080")
public class DestinationController {
    
    @Autowired
    private DestinationRepository destinationRepository;
    
    @GetMapping
    public ResponseEntity<List<Destination>> getAllDestinations(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String category) {
        
        List<Destination> destinations;
        if (country != null && category != null) {
            destinations = destinationRepository.findByCountryAndCategory(country, category);
        } else if (country != null) {
            destinations = destinationRepository.findByCountry(country);
        } else if (category != null) {
            destinations = destinationRepository.findByCategory(category);
        } else {
            destinations = destinationRepository.findAll();
        }
        return ResponseEntity.ok(destinations);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Destination> getDestinationById(@PathVariable Long id) {
        return destinationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Destination> createDestination(@Valid @RequestBody Destination destination) {
        Destination savedDestination = destinationRepository.save(destination);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDestination);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Destination> updateDestination(@PathVariable Long id, @Valid @RequestBody Destination destinationDetails) {
        return destinationRepository.findById(id)
                .map(destination -> {
                    destination.setName(destinationDetails.getName());
                    destination.setCountry(destinationDetails.getCountry());
                    destination.setCity(destinationDetails.getCity());
                    destination.setDescription(destinationDetails.getDescription());
                    destination.setCategory(destinationDetails.getCategory());
                    destination.setLatitude(destinationDetails.getLatitude());
                    destination.setLongitude(destinationDetails.getLongitude());
                    destination.setPopularityScore(destinationDetails.getPopularityScore());
                    destination.setImageUrl(destinationDetails.getImageUrl());
                    return ResponseEntity.ok(destinationRepository.save(destination));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDestination(@PathVariable Long id) {
        if (!destinationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        destinationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}