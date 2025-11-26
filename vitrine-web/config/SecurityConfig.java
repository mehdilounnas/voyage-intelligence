package dz.univ.jijel.vitrineweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public access
                .requestMatchers("/", "/destinations", "/hotels", "/activities", 
                                "/destinations/{id}", "/css/**", "/js/**", "/images/**").permitAll()
                
                // Authenticated operations
                .requestMatchers("/destinations/new", "/destinations/edit/**", 
                                "/destinations/delete/**").authenticated()
                .requestMatchers("/hotels/new", "/hotels/edit/**", 
                                "/hotels/delete/**").authenticated()
                .requestMatchers("/activities/new", "/activities/edit/**", 
                                "/activities/delete/**").authenticated()
                
                // ADMIN only
                .requestMatchers("/users/**").hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .addLogoutHandler(oidcLogoutHandler()) // Add Keycloak logout handler
            );
        
        return http.build();
    }
    
    private LogoutHandler oidcLogoutHandler() {
        return (request, response, authentication) -> {
            // This will properly logout from Keycloak
            if (authentication != null) {
                try {
                    // Clear all cookies
                    if (request.getCookies() != null) {
                        for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                            cookie.setValue("");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                    }
                } catch (Exception e) {
                    // Log error but continue
                }
            }
        };
    }
}
