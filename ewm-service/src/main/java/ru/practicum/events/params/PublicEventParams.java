package ru.practicum.events.params;


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
public class PublicEventParams {
    private String text;                 // текст для поиска (annotation || description)
    private List<Long> categories;       // фильтр по категориям
    private Boolean paid;                // платное/бесплатное
    private String rangeStart;           // формат yyyy-MM-dd HH:mm:ss
    private String rangeEnd;             // формат yyyy-MM-dd HH:mm:ss
    private Boolean onlyAvailable;       // только доступные (не исчерпан лимит)

    /** Sort: "EVENT_DATE" (default) или "VIEWS" */
    private String sort = "EVENT_DATE";

    @Min(0)
    private Integer from = 0;

    @Min(1)
    @Max(1000)
    private Integer size = 10;
}