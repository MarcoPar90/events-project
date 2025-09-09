package com.application.events.services.impl;

import com.application.events.dto.BandsDto;
import com.application.events.dto.PagedResponseDto;
import com.application.events.entity.Bands;
import com.application.events.exception.BadRequestException;
import com.application.events.exception.ConflictException;
import com.application.events.exception.NotFoundException;
import com.application.events.repository.BandsRepository;
import com.application.events.services.BandsService;
import com.application.events.utils.Conversions;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
public class BandsServiceImpl implements BandsService {

    private final BandsRepository bandsRepository;
    private final Conversions conversions;

    @Override
    public PagedResponseDto<BandsDto> getAll(Pageable pageable) {

        Page<Bands> foundBands = bandsRepository.findAll(pageable);
        PagedResponseDto<BandsDto> bands = new PagedResponseDto<>();

        bands.setData(foundBands.getContent().stream()
                .map(conversions::convertBandsEntityToDto)
                .collect(Collectors.toList()));
        bands.setTotalPages(foundBands.getTotalPages());
        bands.setTotalRecords(foundBands.getTotalElements());

        return bands;

    }

    @Override
    public BandsDto getById(int id) {
        Bands band = bandsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("****** Bands with id " + id + " not found ******");
                    return new NotFoundException("Bands with id " + id + " not found");
                });
        return conversions.convertBandsEntityToDto(band);
    }

    @Override
    public BandsDto createNewBand(BandsDto bandsDto) {
        Optional<Bands> band = bandsRepository.findByName(bandsDto.getName());

        if (band.isPresent()) {

            log.warning("****** Bands with name " + bandsDto.getName() + " already exist ******");
            throw new ConflictException("Bands with name " + bandsDto.getName() + " already exist");

        } else {
            Bands newBand = conversions.convertBandsDtoToEntity(bandsDto);

            newBand.setCreateAndUpdateDate(LocalDateTime.now(), LocalDateTime.now());

            Bands saved = bandsRepository.save(newBand);

            log.info("****** Band saved with ID: " + saved.getId() + " ******");

            return conversions.convertBandsEntityToDto(saved);
        }
    }

    @Override
    public BandsDto modifyBand(int id, BandsDto bandsDto) {

        if(bandsDto.getName() == null &&
                bandsDto.getGenre() == null &&
                bandsDto.getDescription() == null &&
                bandsDto.getFormedYear() == 0 &&
                bandsDto.getWebsite() == null ) {
            log.warning("****** Bands with id " + id + " at least one of the fields must be populated ******");
            throw new BadRequestException("At least one of the fields must be populated");
        }

        Bands band = bandsRepository.findById(id)
                .orElseThrow(() -> {
            log.warning("****** Bands with id " + id + " not found ******");
            return new NotFoundException("Bands with id " + id + " not found");
        });

        // copy bandsDto value into entity
        ModelMapper modelMapper = new ModelMapper();
        // ignore null fields
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        // ignore id and merge dto into entity
        modelMapper.typeMap(BandsDto.class, Bands.class)
                .addMappings(mapper -> {
                            mapper.skip(Bands::setId);
                            if(bandsDto != null && bandsDto.getFormedYear() == 0) {
                                mapper.skip(Bands::setFormedYear);
                            }
                        }
                )
                .map(bandsDto, band);
        //set update at
        band.setUpdatedAt(LocalDateTime.now());

        Bands saved = bandsRepository.save(band);

        log.info("****** Band saved with ID: " + saved.getId() + " ******");

        return conversions.convertBandsEntityToDto(saved);
    }

    @Override
    public void deleteBand(int id) {
        Bands band = bandsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("****** Bands with id " + id + " not found ******");
                    return new NotFoundException("Bands with id " + id + " not found");
                });

        try {
            //TODO add remove events with band id
            bandsRepository.delete(band);
        } catch (Exception e) {
            log.severe("Unexpected error deleting band: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
