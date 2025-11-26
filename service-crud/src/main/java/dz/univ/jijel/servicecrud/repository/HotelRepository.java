
package dz.univ.jijel.servicecrud.repository;

import dz.univ.jijel.servicecrud.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByDestinationId(Long destinationId);
    List<Hotel> findByStarsGreaterThanEqual(Integer stars);
    List<Hotel> findByRatingGreaterThanEqual(Double rating);
}

