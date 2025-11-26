package dz.univ.jijel.vitrineweb.controller;

import dz.univ.jijel.vitrineweb.model.*;
import dz.univ.jijel.vitrineweb.service.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class WebController {
    
    @Autowired
    private RestClientService restClientService;
    
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Destination> destinations = restClientService.getAllDestinations();
            model.addAttribute("destinations", destinations);
        } catch (Exception e) {
            model.addAttribute("error", "Impossible de charger les destinations");
        }
        return "index";
    }
    
    @GetMapping("/users")
    public String listUsers(Model model) {
        try {
            List<User> users = restClientService.getAllUsers();
            model.addAttribute("users", users);
        } catch (Exception e) {
            model.addAttribute("error", "Impossible de charger les utilisateurs");
        }
        return "users/list";
    }
    
    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/form";
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        try {
            User user = restClientService.getUserById(id);
            if (user == null) {
                return "redirect:/users?error=UserNotFound";
            }
            model.addAttribute("user", user);
        } catch (Exception e) {
            return "redirect:/users?error=LoadError";
        }
        return "users/form";
    }
    
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            if (user.getId() == null) {
                restClientService.createUser(user);
                redirectAttributes.addFlashAttribute("success", "Profil créé avec succès");
            } else {
                restClientService.updateUser(user.getId(), user);
                redirectAttributes.addFlashAttribute("success", "Profil mis à jour");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la sauvegarde");
        }
        return "redirect:/users";
    }
    
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            restClientService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/users";
    }
    
    @GetMapping("/destinations")
    public String listDestinations(Model model) {
        try {
            List<Destination> destinations = restClientService.getAllDestinations();
            model.addAttribute("destinations", destinations);
        } catch (Exception e) {
            model.addAttribute("error", "Impossible de charger les destinations");
        }
        return "destinations/list";
    }
    
    @GetMapping("/destinations/{id}")
    public String viewDestination(@PathVariable Long id, Model model) {
        try {
            Destination destination = restClientService.getDestinationById(id);
            if (destination == null) {
                return "redirect:/destinations?error=NotFound";
            }
            
            List<Hotel> hotels = restClientService.getHotelsByDestination(id);
            List<Activity> activities = restClientService.getActivitiesByDestination(id);
            
            model.addAttribute("destination", destination);
            model.addAttribute("hotels", hotels);
            model.addAttribute("activities", activities);
        } catch (Exception e) {
            return "redirect:/destinations?error=LoadError";
        }
        return "destinations/view";
    }
    
    @GetMapping("/destinations/new")
    public String newDestinationForm(Model model) {
        model.addAttribute("destination", new Destination());
        return "destinations/form";
    }
    
    @GetMapping("/destinations/edit/{id}")
    public String editDestinationForm(@PathVariable Long id, Model model) {
        try {
            Destination destination = restClientService.getDestinationById(id);
            if (destination == null) {
                return "redirect:/destinations?error=NotFound";
            }
            model.addAttribute("destination", destination);
        } catch (Exception e) {
            return "redirect:/destinations?error=LoadError";
        }
        return "destinations/form";
    }
    
    @PostMapping("/destinations/save")
    public String saveDestination(@ModelAttribute Destination destination, RedirectAttributes redirectAttributes) {
        try {
            if (destination.getId() == null) {
                restClientService.createDestination(destination);
                redirectAttributes.addFlashAttribute("success", "Destination créée avec succès");
            } else {
                restClientService.updateDestination(destination.getId(), destination);
                redirectAttributes.addFlashAttribute("success", "Destination mise à jour");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la sauvegarde");
        }
        return "redirect:/destinations";
    }
    
    @GetMapping("/destinations/delete/{id}")
    public String deleteDestination(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            restClientService.deleteDestination(id);
            redirectAttributes.addFlashAttribute("success", "Destination supprimée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/destinations";
    }
    
    @GetMapping("/destinations/{id}/recommend")
    public String getRecommendation(@PathVariable Long id, 
                                     @RequestParam(required = false) String preferences,
                                     Model model) {
        try {
            Destination destination = restClientService.getDestinationById(id);
            if (destination == null) {
                return "redirect:/destinations?error=NotFound";
            }
            
            IARecommendation recommendation = restClientService.getDestinationRecommendation(destination, preferences);
            
            model.addAttribute("destination", destination);
            model.addAttribute("recommendation", recommendation);
            
            return "destinations/recommend";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la génération de la recommandation");
            return "redirect:/destinations";
        }
    }
    
    @GetMapping("/destinations/{id}/seasonal")
    public String getSeasonalAdvice(@PathVariable Long id,
                                      @RequestParam(defaultValue = "summer") String season,
                                      Model model) {
        try {
            Destination destination = restClientService.getDestinationById(id);
            if (destination == null) {
                return "redirect:/destinations?error=NotFound";
            }
            
            Map<String, Object> advice = restClientService.getSeasonalAdvice(destination, season);
            
            model.addAttribute("destination", destination);
            model.addAttribute("advice", advice);
            model.addAttribute("season", season);
            
            return "destinations/seasonal";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la génération des conseils");
            return "redirect:/destinations";
        }
    }
    
    @GetMapping("/hotels")
    public String listHotels(Model model) {
        try {
            List<Hotel> hotels = restClientService.getAllHotels();
            model.addAttribute("hotels", hotels);
        } catch (Exception e) {
            model.addAttribute("error", "Impossible de charger les hôtels");
        }
        return "hotels/list";
    }
    
    @GetMapping("/hotels/new")
    public String newHotelForm(Model model) {
        try {
            List<Destination> destinations = restClientService.getAllDestinations();
            model.addAttribute("hotel", new Hotel());
            model.addAttribute("destinations", destinations);
        } catch (Exception e) {
            return "redirect:/hotels?error=LoadError";
        }
        return "hotels/form";
    }
    
    @GetMapping("/hotels/edit/{id}")
    public String editHotelForm(@PathVariable Long id, Model model) {
        try {
            Hotel hotel = restClientService.getHotelById(id);
            List<Destination> destinations = restClientService.getAllDestinations();
            
            if (hotel == null) {
                return "redirect:/hotels?error=NotFound";
            }
            
            model.addAttribute("hotel", hotel);
            model.addAttribute("destinations", destinations);
        } catch (Exception e) {
            return "redirect:/hotels?error=LoadError";
        }
        return "hotels/form";
    }
    
    @PostMapping("/hotels/save")
    public String saveHotel(@ModelAttribute Hotel hotel, 
                             @RequestParam Long destinationId,
                             RedirectAttributes redirectAttributes) {
        try {
            Destination destination = restClientService.getDestinationById(destinationId);
            hotel.setDestination(destination);
            
            if (hotel.getId() == null) {
                restClientService.createHotel(hotel);
                redirectAttributes.addFlashAttribute("success", "Hôtel créé avec succès");
            } else {
                restClientService.updateHotel(hotel.getId(), hotel);
                redirectAttributes.addFlashAttribute("success", "Hôtel mis à jour");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la sauvegarde");
        }
        return "redirect:/hotels";
    }
    
    @GetMapping("/hotels/delete/{id}")
    public String deleteHotel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            restClientService.deleteHotel(id);
            redirectAttributes.addFlashAttribute("success", "Hôtel supprimé");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/hotels";
    }
    
    @GetMapping("/activities")
    public String listActivities(Model model) {
        try {
            List<Activity> activities = restClientService.getAllActivities();
            model.addAttribute("activities", activities);
        } catch (Exception e) {
            model.addAttribute("error", "Impossible de charger les activités");
        }
        return "activities/list";
    }
    
    @GetMapping("/activities/new")
    public String newActivityForm(Model model) {
        try {
            List<Destination> destinations = restClientService.getAllDestinations();
            model.addAttribute("activity", new Activity());
            model.addAttribute("destinations", destinations);
        } catch (Exception e) {
            return "redirect:/activities?error=LoadError";
        }
        return "activities/form";
    }
    
    @GetMapping("/activities/edit/{id}")
    public String editActivityForm(@PathVariable Long id, Model model) {
        try {
            Activity activity = restClientService.getActivityById(id);
            List<Destination> destinations = restClientService.getAllDestinations();
            
            if (activity == null) {
                return "redirect:/activities?error=NotFound";
            }
            
            model.addAttribute("activity", activity);
            model.addAttribute("destinations", destinations);
        } catch (Exception e) {
            return "redirect:/activities?error=LoadError";
        }
        return "activities/form";
    }
    
    @PostMapping("/activities/save")
    public String saveActivity(@ModelAttribute Activity activity,
                                @RequestParam Long destinationId,
                                RedirectAttributes redirectAttributes) {
        try {
            Destination destination = restClientService.getDestinationById(destinationId);
            activity.setDestination(destination);
            
            if (activity.getId() == null) {
                restClientService.createActivity(activity);
                redirectAttributes.addFlashAttribute("success", "Activité créée avec succès");
            } else {
                restClientService.updateActivity(activity.getId(), activity);
                redirectAttributes.addFlashAttribute("success", "Activité mise à jour");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la sauvegarde");
        }
        return "redirect:/activities";
    }
    
    @GetMapping("/activities/delete/{id}")
    public String deleteActivity(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            restClientService.deleteActivity(id);
            redirectAttributes.addFlashAttribute("success", "Activité supprimée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
        }
        return "redirect:/activities";
    }
}