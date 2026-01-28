package com.svalero.cityEvents.service;

import com.svalero.cityEvents.domain.Artist;
import com.svalero.cityEvents.dto.ArtistOutDto;
import com.svalero.cityEvents.dto.EventOutDto;
import com.svalero.cityEvents.exception.ArtistNotFoundException;
import com.svalero.cityEvents.repository.ArtistRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

    public List<ArtistOutDto> findAll(String type, Boolean active, Boolean orderByFollowers) {

        List<Artist> allArtists;

        if (type != null && !type.isEmpty()) {
            allArtists = artistRepository.findByType(type);
        } else if (active != null && active) {
            allArtists = artistRepository.findByActiveTrue();
        } else if (orderByFollowers != null && orderByFollowers) {
            allArtists = artistRepository.findAllByOrderByFollowersDesc();
        } else {
            allArtists = artistRepository.findAll();
        }

        return modelMapper.map(allArtists, new TypeToken<List<ArtistOutDto>>() {}.getType());
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
