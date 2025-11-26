package dz.univ.jijel.vitrineweb.service;

import dz.univ.jijel.vitrineweb.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class RestClientService {
    
    private final WebClient crudWebClient;
    private final WebClient iaWebClient;
    
    public RestClientService(
            @Value("${service.crud.url}") String crudBaseUrl,
            @Value("${service.ia.url}") String iaBaseUrl) {
        this.crudWebClient = WebClient.builder().baseUrl(crudBaseUrl).build();
        this.iaWebClient = WebClient.builder().baseUrl(iaBaseUrl).build();
    }
    
    // ============ USERS ============
    
    public List<User> getAllUsers() {
        return crudWebClient.get()
                .uri("/users")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error fetching users")))
                .bodyToMono(new ParameterizedTypeReference<List<User>>() {})
                .onErrorReturn(List.of())
                .block();
    }
    
    public User getUserById(Long id) {
        try {
            return crudWebClient.get()
                    .uri("/users/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), 
                        response -> Mono.error(new RuntimeException("User not found: " + id)))
                    .onStatus(status -> status.is5xxServerError(), 
                        response -> Mono.error(new RuntimeException("Server error")))
                    .bodyToMono(User.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
    
    public User createUser(User user) {
        return crudWebClient.post()
                .uri("/users")
                .bodyValue(user)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error creating user")))
                .bodyToMono(User.class)
                .block();
    }
    
    public User updateUser(Long id, User user) {
        return crudWebClient.put()
                .uri("/users/{id}", id)
                .bodyValue(user)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error updating user")))
                .bodyToMono(User.class)
                .block();
    }
    
    public void deleteUser(Long id) {
        crudWebClient.delete()
                .uri("/users/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error deleting user")))
                .toBodilessEntity()
                .block();
    }
    
    // ============ DESTINATIONS ============
    
    public List<Destination> getAllDestinations() {
        return crudWebClient.get()
                .uri("/destinations")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error fetching destinations")))
                .bodyToMono(new ParameterizedTypeReference<List<Destination>>() {})
                .onErrorReturn(List.of())
                .block();
    }
    
    public Destination getDestinationById(Long id) {
        try {
            return crudWebClient.get()
                    .uri("/destinations/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), 
                        response -> Mono.error(new RuntimeException("Destination not found: " + id)))
                    .onStatus(status -> status.is5xxServerError(), 
                        response -> Mono.error(new RuntimeException("Server error")))
                    .bodyToMono(Destination.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
    
    public Destination createDestination(Destination destination) {
        return crudWebClient.post()
                .uri("/destinations")
                .bodyValue(destination)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error creating destination")))
                .bodyToMono(Destination.class)
                .block();
    }
    
    public Destination updateDestination(Long id, Destination destination) {
        return crudWebClient.put()
                .uri("/destinations/{id}", id)
                .bodyValue(destination)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error updating destination")))
                .bodyToMono(Destination.class)
                .block();
    }
    
    public void deleteDestination(Long id) {
        crudWebClient.delete()
                .uri("/destinations/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error deleting destination")))
                .toBodilessEntity()
                .block();
    }
    
    // ============ HOTELS ============
    
    public List<Hotel> getAllHotels() {
        return crudWebClient.get()
                .uri("/hotels")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error fetching hotels")))
                .bodyToMono(new ParameterizedTypeReference<List<Hotel>>() {})
                .onErrorReturn(List.of())
                .block();
    }
    
    public List<Hotel> getHotelsByDestination(Long destinationId) {
        return crudWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/hotels")
                        .queryParam("destinationId", destinationId)
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error fetching hotels")))
                .bodyToMono(new ParameterizedTypeReference<List<Hotel>>() {})
                .onErrorReturn(List.of())
                .block();
    }
    
    public Hotel getHotelById(Long id) {
        try {
            return crudWebClient.get()
                    .uri("/hotels/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), 
                        response -> Mono.error(new RuntimeException("Hotel not found: " + id)))
                    .onStatus(status -> status.is5xxServerError(), 
                        response -> Mono.error(new RuntimeException("Server error")))
                    .bodyToMono(Hotel.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
    
    public Hotel createHotel(Hotel hotel) {
        return crudWebClient.post()
                .uri("/hotels")
                .bodyValue(hotel)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error creating hotel")))
                .bodyToMono(Hotel.class)
                .block();
    }
    
    public Hotel updateHotel(Long id, Hotel hotel) {
        return crudWebClient.put()
                .uri("/hotels/{id}", id)
                .bodyValue(hotel)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error updating hotel")))
                .bodyToMono(Hotel.class)
                .block();
    }
    
    public void deleteHotel(Long id) {
        crudWebClient.delete()
                .uri("/hotels/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error deleting hotel")))
                .toBodilessEntity()
                .block();
    }
    
    // ============ ACTIVITIES ============
    
    public List<Activity> getAllActivities() {
        return crudWebClient.get()
                .uri("/activities")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error fetching activities")))
                .bodyToMono(new ParameterizedTypeReference<List<Activity>>() {})
                .onErrorReturn(List.of())
                .block();
    }
    
    public List<Activity> getActivitiesByDestination(Long destinationId) {
        return crudWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/activities")
                        .queryParam("destinationId", destinationId)
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error fetching activities")))
                .bodyToMono(new ParameterizedTypeReference<List<Activity>>() {})
                .onErrorReturn(List.of())
                .block();
    }
    
    public Activity getActivityById(Long id) {
        try {
            return crudWebClient.get()
                    .uri("/activities/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), 
                        response -> Mono.error(new RuntimeException("Activity not found: " + id)))
                    .onStatus(status -> status.is5xxServerError(), 
                        response -> Mono.error(new RuntimeException("Server error")))
                    .bodyToMono(Activity.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
    
    public Activity createActivity(Activity activity) {
        return crudWebClient.post()
                .uri("/activities")
                .bodyValue(activity)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error creating activity")))
                .bodyToMono(Activity.class)
                .block();
    }
    
    public Activity updateActivity(Long id, Activity activity) {
        return crudWebClient.put()
                .uri("/activities/{id}", id)
                .bodyValue(activity)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error updating activity")))
                .bodyToMono(Activity.class)
                .block();
    }
    
    public void deleteActivity(Long id) {
        crudWebClient.delete()
                .uri("/activities/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Error deleting activity")))
                .toBodilessEntity()
                .block();
    }
    
    // ============ IA SERVICE ============
    
    public IARecommendation getDestinationRecommendation(Destination destination, String userPreferences) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "destination", destination,
                    "userPreferences", userPreferences != null ? userPreferences : ""
            );
            
            return iaWebClient.post()
                    .uri("/recommend")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                        System.err.println("IA Service error: " + response.statusCode());
                        return Mono.error(new RuntimeException("IA Service unavailable"));
                    })
                    .bodyToMono(IARecommendation.class)
                    .block();
        } catch (WebClientResponseException ex) {
            System.err.println("IA Service connection error: " + ex.getMessage());
            return IARecommendation.builder()
                    .destination(destination.getName())
                    .recommendation("Découvrez cette magnifique destination ! Service IA temporairement indisponible.")
                    .build();
        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
            return IARecommendation.builder()
                    .destination(destination.getName())
                    .recommendation("Découvrez cette magnifique destination !")
                    .build();
        }
    }
    
    public Map<String, Object> getSeasonalAdvice(Destination destination, String season) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "destination", destination,
                    "season", season
            );
            
            return iaWebClient.post()
                    .uri("/seasonal-advice")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                        System.err.println("IA Service error for seasonal advice: " + response.statusCode());
                        return Mono.error(new RuntimeException("IA Service unavailable"));
                    })
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (Exception ex) {
            System.err.println("Seasonal advice error: " + ex.getMessage());
            return Map.of("advice", "Consultez un guide local pour plus d'informations. Service IA temporairement indisponible.");
        }
    }
}
