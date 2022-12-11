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
        boolean intNull = false;
        try {
            fis = new FileInputStream(propertiesPath.toFile());
            property.load(fis);

            java.lang.Class<? extends Class> cls1 = cls.getClass();
            Field[] fs = cls1.getDeclaredFields(); // получили массив с объектами Field, соответствующие полям класса cls
            String stringProperty = "";
            Instant timeProperty = Instant.now();
            Integer  numberProperty = -1 ;
            Pattern attributePatternName = Pattern.compile("[n][a][m][e]=\"([^\"]*)\"");
            Pattern attributePatternFormat = Pattern.compile("[r][m][a][t]\\s*=\\s*\"([^\"]*)\"");
            for (int i = 0; i < fs.length; i++) {
                Annotation[] annotations =  fs[i].getAnnotations();
                Matcher matcherName = attributePatternName.matcher(Arrays.toString(annotations));
                Matcher matcherFormat = attributePatternFormat.matcher(Arrays.toString(annotations));
                if(matcherName.find()){//нашлась анотация
                    if (fs[i].getType()== String.class) {//нашли стринг
                        stringProperty = property.getProperty(matcherName.group(1));
                    } else if (fs[i].getType() == Integer.class || fs[i].getType()== int.class) {//нашли инт
                        if(property.getProperty(matcherName.group(1)).equals("")){
                            intNull = true;
                        }else {
                            numberProperty = Integer.parseInt(property.getProperty(matcherName.group(1)));
                        }
                    } else if (fs[i].getType() == Instant.class && matcherFormat.find()) {//нашли дату
                        SimpleDateFormat sdf1 = new SimpleDateFormat(matcherFormat.group(1));
                        timeProperty = sdf1.parse(property.getProperty(matcherName.group(1))).toInstant();
                    }
                }else {//по имени обьекта
                    if (fs[i].getType()== String.class) {
                        stringProperty = property.getProperty(fs[i].getName());
                    } else if (fs[i].getType().equals(numberProperty.getClass()) || fs[i].getType()== int.class) {
                        numberProperty = Integer.parseInt(property.getProperty(fs[i].getName()));
                    } else if (fs[i].getType() == Instant.class) {
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        timeProperty = sdf1.parse(property.getProperty(fs[i].getName())).toInstant();
                    }
                }
            }
            if(intNull || stringProperty.equals("") || timeProperty==Instant.now()){
                throw new IllegalArgumentException("ОШИБКА: Один из параметров пуст!");
            }

            sa = new Class<T>();
            sa.setNumberProperty(numberProperty);
            sa.setStringProperty(stringProperty);
            sa.setTimeProperty(timeProperty);

        } catch (IOException e) {
            throw new IllegalArgumentException("ОШИБКА: Проблемы с файлом!");
        } catch (ParseException e) {
            throw new IllegalArgumentException("ОШИБКА: Проблемы с датой!");
        }
        return (T) sa;
    }
}
