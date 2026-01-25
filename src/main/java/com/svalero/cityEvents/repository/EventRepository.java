package com.svalero.cityEvents.repository;

import com.svalero.cityEvents.domain.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findAll();
    List<Event> findByCategory(String category);
    List<Event> findByLocation_Name(String locationName);
    List<Event> findByPriceLessThanEqualOrderByPriceAsc(Float price);
}
