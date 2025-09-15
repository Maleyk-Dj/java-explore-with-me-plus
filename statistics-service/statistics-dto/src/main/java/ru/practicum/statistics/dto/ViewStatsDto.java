package ru.practicum.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ViewStatsDto {

    String app;
    String uri;
    long hits;
}
