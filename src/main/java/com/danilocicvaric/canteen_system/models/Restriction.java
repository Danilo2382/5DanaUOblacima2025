package com.danilocicvaric.canteen_system.models;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restrictions")
@Getter
@Setter
@NoArgsConstructor
public class Restriction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "canteen_id", nullable = false)
    private Canteen canteen;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "restriction_working_hours",
            joinColumns = @JoinColumn(name = "restriction_id")
    )
    private List<@Valid CanteenWorkingHour> temporaryWorkingHours = new ArrayList<>();
}