package com.application.events.entity;

import com.application.events.enumeration.EventStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "events")
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "event_code")
    @NotNull
    private String eventCode;

    @Column(name = "title")
    @NotNull
    private String title;

    @Column(name = "description")
    @Size(min = 10, max = 250, message = "Description must be between 10 and 250 characters")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "location")
    private String location;

    @Column(name = "total_seats")
    @NotNull
    @Min(value = 100, message = "you must add at least 100 places")
    private int totalSeats;

    @Column(name = "available_seats")
    @NotNull
    private int availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status")
    @NotNull
    private EventStatus eventStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "band_id")
    private Bands band;

    public void setDateAndBand(LocalDateTime createdAt, LocalDateTime updatedAt, Bands band) {
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
        this.setBand(band);
    };

}
