package ru.practicum.ewm.params;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventParams {
    private List<Long> users;
    private List<String> states;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;

    @Min(0)
    private Integer from = 0;

    @Min(1)
    @Max(1000)
    private Integer size = 10;
}
