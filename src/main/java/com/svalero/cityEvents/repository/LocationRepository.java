package com.svalero.cityEvents.repository;

import com.svalero.cityEvents.domain.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationRepository extends CrudRepository<Location, Long> {

    List<Location> findAll(); //con esto declaramos que nos haga un select * from Location
    List<Location> findByCategory(String category);
    List<Location> findByDisabledAccessTrue();
}
