package com.svalero.cityEvents.repository;

import com.svalero.cityEvents.domain.Artist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends CrudRepository<Artist, Long> {
    List<Artist> findAll();
    List<Artist> findByType(String type);
    List<Artist> findByActiveTrue();

}
