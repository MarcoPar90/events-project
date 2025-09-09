package com.application.events.services.impl;

import com.application.events.dto.EventsDto;
import com.application.events.dto.PagedResponseDto;
import com.application.events.entity.Bands;
import com.application.events.entity.Events;
import com.application.events.enumeration.Role;
import com.application.events.exception.BadRequestException;
import com.application.events.exception.ConflictException;
import com.application.events.exception.NotFoundException;
import com.application.events.repository.BandsRepository;
import com.application.events.repository.EventsRepository;
import com.application.events.services.EventsService;
import com.application.events.utils.Conversions;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
public class EventsServiceImpl implements EventsService {

    private final EventsRepository eventsRepository;
    private final BandsRepository bandsRepository;
    private final Conversions conversions;

    @Override
    public PagedResponseDto<EventsDto> getAll(Pageable pageable) {
        Page<Events> foundEvents = eventsRepository.findAll(pageable);
        PagedResponseDto<EventsDto> events = new PagedResponseDto<>();

        events.setData(foundEvents.getContent()
                .stream()
                .map(conversions::convertEventsEntityToDto)
                .collect(Collectors.toList()));
        events.setTotalPages(foundEvents.getTotalPages());
        events.setTotalRecords(foundEvents.getTotalElements());
        return events;
    }

    @Override
    public EventsDto getById(int id) {
        Events events = eventsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("****** event with id " + id + " not found ******");
                    return new NotFoundException("event with id " + id + " not found");
                });
        log.info("****** event with id " + id + " found ******");
        return conversions.convertEventsEntityToDto(events);
    }

    @Override
    public List<EventsDto> findByIds(List<Integer> ids) {
        List<Events> events = eventsRepository.findAllById(ids);
        return events.stream()
                .map(conversions::convertEventsEntityToDto)
                .toList();
    }

    @Override
    public EventsDto createEvent(EventsDto eventsDto) {
        Optional<Events> event = eventsRepository.findByTitleOrEventCode(eventsDto.getTitle(), eventsDto.getEventCode());

        if(event.isPresent()) {
            if(event.get().getTitle().equals(eventsDto.getTitle()) && event.get().getEventCode().equals(eventsDto.getEventCode())) {
                log.warning("****** Event with title " + eventsDto.getTitle() + " and code " + eventsDto.getEventCode() + " already exist ******");
                throw new ConflictException("Event with title " + eventsDto.getTitle() + " and code " + eventsDto.getEventCode() + " already exist");
            } else if(event.get().getTitle().equals(eventsDto.getTitle())) {
                log.warning("****** Event with title " + eventsDto.getTitle() + " already exist ******");
                throw new ConflictException("Event with title " + eventsDto.getTitle() + " already exist");
            } else  {
                log.warning("****** Event with code " + eventsDto.getEventCode() + " already exist ******");
                throw new ConflictException("Event with code " + eventsDto.getEventCode() + " already exist");
            }
        } else {
            Events newEvent = conversions.convertEventsDtoToEntity(eventsDto);

            Bands bands = bandsRepository.findById(eventsDto.getBand().getId())
                    .orElseThrow(() -> {
                        log.warning("****** Bands with id " + eventsDto.getBand().getId() + " not found ******");
                        return new NotFoundException("Bands with id " + eventsDto.getBand().getId() + " not found");
                    });

            newEvent.setDateAndBand(LocalDateTime.now(), LocalDateTime.now(), bands);

            Events saved = eventsRepository.save(newEvent);

            log.info("****** Event saved with ID: " + saved.getId() + " ******");

            return conversions.convertEventsEntityToDto(saved);
        }
    }

    @Override
    public EventsDto modifyEvent(EventsDto eventsDto, int id, Role role) {
        if(eventsDto.getTitle() == null &&
                eventsDto.getDescription() == null &&
                eventsDto.getEventDate() == null &&
                eventsDto.getLocation() == null &&
                eventsDto.getTotalSeats() < 0 &&
                eventsDto.getAvailableSeats() < 0 &&
                eventsDto.getBand() == null
        ) {
            log.warning("****** Event with id " + id + " at least one of the fields must be populated ******");
            throw new BadRequestException("At least one of the fields must be populated");
        }

        Events events = eventsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("****** Events with id " + id + " not found ******");
                    return new NotFoundException("Events with id " + id + " not found");
                });

        Bands newBand = bandsRepository.findById(eventsDto.getBand().getId())
                .orElseThrow(() -> {
                    log.warning("****** Bands with id " + eventsDto.getBand().getId() + " not found ******");
                    return new NotFoundException("Bands with id " + eventsDto.getBand().getId() + " not found");
                });

        if(role == Role.ADMIN) {
            // copy eventsDto value into entity
            ModelMapper modelMapper = new ModelMapper();
            // ignore null fields
            modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            // ignore id and merge dto into entity
            modelMapper.typeMap(EventsDto.class, Events.class)
                    .addMappings(mapper -> {
                                mapper.skip(Events::setId);
                                if(newBand != null && (!events.getBand().getId().equals(newBand.getId()))) {
                                    mapper.map(src -> newBand, Events::setBand);
                                }
                            }
                    )
                    .map(eventsDto, events);
        } else {
            events.setAvailableSeats(eventsDto.getAvailableSeats());
        }

        events.setUpdatedAt(LocalDateTime.now());

        Events saved = eventsRepository.save(events);

        log.info("****** Band saved with ID: " + saved.getId() + " ******");

        return conversions.convertEventsEntityToDto(saved);
    }

    @Override
    public void deleteBand(int id) {
        Events events = eventsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("****** Events with id " + id + " not found ******");
                    return new NotFoundException("Events with id " + id + " not found");
                });
        try {
            eventsRepository.delete(events);
        } catch (Exception e) {
            log.severe("Unexpected error deleting event: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
