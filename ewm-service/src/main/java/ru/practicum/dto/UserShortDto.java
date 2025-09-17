package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/*скопировать или удалить при слитии в ветку*/
public class UserShortDto {
    private Long id;
    private String name;
}
