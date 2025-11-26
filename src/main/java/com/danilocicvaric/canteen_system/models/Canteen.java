package com.danilocicvaric.canteen_system.models;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "canteens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"}),
        @UniqueConstraint(columnNames = {"location"})
})
@Getter
@Setter
@NoArgsConstructor
public class Canteen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    @Min(1)
    private int capacity;

    @ElementCollection
    @CollectionTable(name = "canteen_working_hours", joinColumns = @JoinColumn(name = "canteen_id"))
    @OrderColumn(name = "position")
    private List<@Valid @NotNull CanteenWorkingHour> workingHours = new ArrayList<>();
}
