package ru.practicum.util;

import lombok.experimental.UtilityClass;
import ru.practicum.validation.FieldDescription;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class Reflection {
    public static <T> String[] getIgnoreProperties(T obj) {
        List<String> listIgnoreProperties = new ArrayList<>();

        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(FieldDescription.class)) {
                FieldDescription annotation = field.getAnnotation(FieldDescription.class);
                if (!annotation.changeByCopy()) {
                    listIgnoreProperties.add(field.getName());
                } else {
                    try {
                        field.setAccessible(true);
                        if (field.get(obj) == null) listIgnoreProperties.add(field.getName());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return listIgnoreProperties.toArray(new String[0]);
    }
}
