package org.example.task2;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.lang.String;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.time.Instant;

public class PropertiesFile {
    public static class Class<T> {
        @Property(name="stringProperty")
        private String  stringProperty;
        @Property(name="numberProperty")
        private int  numberProperty;
        @Property(name="timeProperty", format = "dd.MM.yyyy HH:mm")
        private Instant timeProperty;

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;}

        public void setNumberProperty(int numberProperty) {
            this.numberProperty = numberProperty;}

        public void setTimeProperty(Instant timeProperty) {
            this.timeProperty = timeProperty;}
    }
    public static <T>T loadFromProperties(Class<T> cls, Path propertiesPath) {
        FileInputStream fis;
        java.util.Properties property = new java.util.Properties();
        Class<T> sa;
        try {
            fis = new FileInputStream(propertiesPath.toFile());
            property.load(fis);

            java.lang.Class<? extends Class> cls1 = cls.getClass();
            Field[] fs = cls1.getDeclaredFields(); // получили массив с объектами Field, соответствующие полям класса cls
            String stringProperty = "";
            Instant timeProperty = Instant.now();
            int numberProperty = 1 ;
            Pattern attributePatternName = Pattern.compile("[n][a][m][e]=\"([^\"]*)\"");
            Pattern attributePatternFormat = Pattern.compile("[r][m][a][t]\\s*=\\s*\"([^\"]*)\"");
            for (int i = 0; i < fs.length; i++) {
                Annotation[] annotations =  fs[i].getAnnotations();
                Matcher matcherName = attributePatternName.matcher(Arrays.toString(annotations));
                Matcher matcherFormat = attributePatternFormat.matcher(Arrays.toString(annotations));
                if(matcherName.find()){//нашлась анотация
                    if (fs[i].getType().equals(stringProperty.getClass())) {
                        stringProperty = property.getProperty(matcherName.group(1));
                    } else if (fs[i].getType().equals(numberProperty)) {
                        numberProperty = Integer.parseInt(property.getProperty(matcherName.group(1)));
                    } else if (fs[i].getType().equals(timeProperty.getClass()) && matcherFormat.find()) {
                        SimpleDateFormat sdf1 = new SimpleDateFormat(matcherFormat.group(1));
                        timeProperty = sdf1.parse(property.getProperty(matcherName.group(1))).toInstant();
                    }
                }else {//по имени обьекта
                    if (fs[i].getType().equals(stringProperty.getClass())) {
                        stringProperty = property.getProperty(fs[i].getName());
                    } else if (fs[i].getType().equals(numberProperty)) {
                        numberProperty = Integer.parseInt(property.getProperty(fs[i].getName()));
                    } else if (fs[i].getType().equals(timeProperty.getClass())) {
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        timeProperty = sdf1.parse(property.getProperty(fs[i].getName())).toInstant();
                    }
                }
            }

            sa = new Class<T>();
            sa.setNumberProperty(numberProperty);
            sa.setStringProperty(stringProperty);
            sa.setTimeProperty(timeProperty);

        } catch (IOException e) {
            throw new IllegalArgumentException("ОШИБКА: Файл свойств отсуствует!");
        } catch (ParseException e) {
            throw new IllegalArgumentException("ОШИБКА: Не тот формат даты!");
        }
        return (T) sa;
    }
}
