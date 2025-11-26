
package dz.univ.jijel.servicecrud.controller;

import dz.univ.jijel.servicecrud.entity.Hotel;
import dz.univ.jijel.servicecrud.repository.HotelRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "http://localhost:8080")
public class HotelController {
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels(
            @RequestParam(required = false) Long destinationId) {
        
        List<Hotel> hotels;
        if (destinationId != null) {
            hotels = hotelRepository.findByDestinationId(destinationId);
        } else {
            hotels = hotelRepository.findAll();
        }
        return ResponseEntity.ok(hotels);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
        return hotelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Hotel> createHotel(@Valid @RequestBody Hotel hotel) {
        Hotel savedHotel = hotelRepository.save(hotel);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHotel);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @Valid @RequestBody Hotel hotelDetails) {
        return hotelRepository.findById(id)
                .map(hotel -> {
                    hotel.setName(hotelDetails.getName());
                    hotel.setDestination(hotelDetails.getDestination());
                    hotel.setAddress(hotelDetails.getAddress());
                    hotel.setStars(hotelDetails.getStars());
                    hotel.setPricePerNight(hotelDetails.getPricePerNight());
                    hotel.setAmenities(hotelDetails.getAmenities());
                    hotel.setRating(hotelDetails.getRating());
                    hotel.setImageUrl(hotelDetails.getImageUrl());
                    return ResponseEntity.ok(hotelRepository.save(hotel));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        if (!hotelRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        hotelRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

// ====================================