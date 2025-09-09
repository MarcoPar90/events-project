package com.application.events.services;

import com.application.events.dto.EventsDto;
import com.application.events.dto.PagedResponseDto;

import com.application.events.enumeration.Role;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventsService {

    PagedResponseDto<EventsDto> getAll(Pageable pageable);
    EventsDto getById(int id);
    List<EventsDto> findByIds(List<Integer> ids);
    EventsDto createEvent(EventsDto eventsDto);
    EventsDto modifyEvent(EventsDto eventsDto, int id, Role role);
    void deleteBand(int id);
}
