package com.application.events.utils;

import com.application.events.dto.BandsDto;
import com.application.events.dto.EventsDto;
import com.application.events.entity.Bands;
import com.application.events.entity.Events;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Conversions {
    private final ModelMapper modelMapper;

    //events
    public EventsDto convertEventsEntityToDto(Events event) {
        return modelMapper.map(event, EventsDto.class);
    }

    public Events convertEventsDtoToEntity(EventsDto eventsDto) {
        return modelMapper.map(eventsDto, Events.class);
    }

    //bands
    public BandsDto convertBandsEntityToDto(Bands bands) {
        return modelMapper.map(bands, BandsDto.class);
    }

    public Bands convertBandsDtoToEntity(BandsDto bandsDto) {
        return modelMapper.map(bandsDto, Bands.class);
    }
}
