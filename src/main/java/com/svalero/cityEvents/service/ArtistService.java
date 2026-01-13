package com.svalero.cityEvents.service;

import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.exception.ArtistNotFoundException;
import com.svalero.cityEvents.repository.ArtistRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Artist add(Artist artist) {
        return artistRepository.save(artist);
    }

    public void delete(long id) throws ArtistNotFoundException {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(ArtistNotFoundException::new);
        artistRepository.delete(artist);
    }

    public List<Artist> findAll() {
        return artistRepository.findAll();
    }

    public List<Artist> findByType(String type) {
        return artistRepository.findByType(type);
    }

    public List<Artist> findByArtistActiveTrue() {
        return artistRepository.findByActiveTrue();
    }

    public Artist findArtistById(long id) throws ArtistNotFoundException {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(ArtistNotFoundException::new);
        return artist;
    }

    public List<Artist> findAllArtistsById(List<Long> ids) {
        return (List<Artist>) artistRepository.findAllById(ids);
    }

    public Artist modify(long id, Artist artist) throws ArtistNotFoundException {
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(ArtistNotFoundException::new);

        modelMapper.map(artist, existingArtist);
        existingArtist.setId(id);

        return artistRepository.save(existingArtist);
    }
}
