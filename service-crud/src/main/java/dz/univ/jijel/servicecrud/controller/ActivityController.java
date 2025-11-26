
package dz.univ.jijel.servicecrud.controller;

import dz.univ.jijel.servicecrud.entity.Activity;
import dz.univ.jijel.servicecrud.repository.ActivityRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "http://localhost:8080")
public class ActivityController {
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities(
            @RequestParam(required = false) Long destinationId) {
        
        List<Activity> activities;
        if (destinationId != null) {
            activities = activityRepository.findByDestinationId(destinationId);
        } else {
            activities = activityRepository.findAll();
        }
        return ResponseEntity.ok(activities);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Activity> createActivity(@Valid @RequestBody Activity activity) {
        Activity savedActivity = activityRepository.save(activity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedActivity);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @Valid @RequestBody Activity activityDetails) {
        return activityRepository.findById(id)
                .map(activity -> {
                    activity.setName(activityDetails.getName());
                    activity.setDestination(activityDetails.getDestination());
                    activity.setDescription(activityDetails.getDescription());
                    activity.setType(activityDetails.getType());
                    activity.setPrice(activityDetails.getPrice());
                    activity.setDuration(activityDetails.getDuration());
                    activity.setRating(activityDetails.getRating());
                    activity.setImageUrl(activityDetails.getImageUrl());
                    return ResponseEntity.ok(activityRepository.save(activity));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        if (!activityRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        activityRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}