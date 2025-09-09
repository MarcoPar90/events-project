package com.application.events.services;

import com.application.events.dto.BandsDto;
import com.application.events.dto.PagedResponseDto;
import org.springframework.data.domain.Pageable;

public interface BandsService {

    PagedResponseDto<BandsDto> getAll(Pageable pageable);
    BandsDto getById(int id);
    BandsDto createNewBand(BandsDto bandsDto);
    BandsDto modifyBand(int id, BandsDto bandsDto);
    void deleteBand(int id);
}
