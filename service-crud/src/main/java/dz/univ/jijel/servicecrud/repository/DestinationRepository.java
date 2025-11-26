
package dz.univ.jijel.servicecrud.repository;

import dz.univ.jijel.servicecrud.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
    List<Destination> findByCountry(String country);
    List<Destination> findByCategory(String category);
    List<Destination> findByCountryAndCategory(String country, String category);
}
