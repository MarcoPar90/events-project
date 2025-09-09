package com.application.booking.utils;

import com.application.booking.dto.templateDto.EventsDto;
import com.application.booking.dto.templateDto.UserDto;
import com.application.booking.exception.NotFoundException;
import com.application.booking.exception.ServiceUnvailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RestTemplateApis {

    @Value("${application.users.url}")
    private String usersUrl;

    @Value("${application.events.url}")
    private String eventsUrl;

    private final RestTemplate restTemplate;

    public UserDto getUser(String authorizationHeader, String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<UserDto> response = restTemplate.exchange(
                    String.format(usersUrl + "/%s", path),
                    HttpMethod.GET,
                    requestEntity,
                    UserDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("User not found");
        } catch (ResourceAccessException e) {
            throw new ServiceUnvailableException("Service unvailable");
        }
    }

    public EventsDto getEvents(String authorizationHeader, int eventCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);

        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<EventsDto> response = restTemplate.exchange(
                    String.format(eventsUrl + "/%d", eventCode),
                    HttpMethod.GET,
                    requestEntity,
                    EventsDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException(
                    String.format("Event %d not found", eventCode)
            );
        } catch (ResourceAccessException e) {
            throw new ServiceUnvailableException("Service unvailable");
        }
    }

    public EventsDto modifyEvent(String authorizationHeader, int eventCode, EventsDto body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);

        HttpEntity<EventsDto> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<EventsDto> response = restTemplate.exchange(
                    String.format(eventsUrl + "/%d", eventCode),
                    HttpMethod.PATCH,
                    requestEntity,
                    EventsDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException(
                    String.format("Event %d not found", eventCode)
            );
        } catch (ResourceAccessException e) {
            throw new ServiceUnvailableException("Service unvailable");
        }
    }

    public List<EventsDto> getEventsList(String authorizationHeader, List<Integer> eventIds) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);

        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            String idsParam = eventIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            ResponseEntity<EventsDto[]> response = restTemplate.exchange(
                    String.format(eventsUrl + "?ids=%s", idsParam),
                    HttpMethod.GET,
                    requestEntity,
                    EventsDto[].class
            );

            return Arrays.asList(response.getBody());
        } catch (ResourceAccessException e) {
            throw new ServiceUnvailableException("Service unavailable");
        }
    }
}
