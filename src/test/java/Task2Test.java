import org.junit.jupiter.api.Assertions;
import org.example.task2.PropertiesFile;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.example.task2.PropertiesFile.loadFromProperties;

public class Task2Test {
    @Test
    public void testProperties() {
        //given
        Path testFilePath = Paths.get("");
        PropertiesFile.Class cls = new  PropertiesFile.Class<>();
        IllegalArgumentException a = Assertions.assertThrows(IllegalArgumentException.class,()-> {
            loadFromProperties(  cls, testFilePath);
        });
        Assertions.assertEquals("ОШИБКА: Файл свойств отсуствует!",a.getMessage());
    }
    @Test
    public void testPropertiesBadDate() {
        //given
        Path testFilePath = Paths.get("src/main/resources/configBadDate.properties");
        PropertiesFile.Class cls = new  PropertiesFile.Class<>();
        IllegalArgumentException a = Assertions.assertThrows(IllegalArgumentException.class,()-> {
            loadFromProperties(  cls, testFilePath);
        });
        Assertions.assertEquals("ОШИБКА: Не тот формат даты!",a.getMessage());
    }
}

