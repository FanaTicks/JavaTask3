import org.junit.jupiter.api.Assertions;
import org.example.task2.PropertiesFile;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.example.task2.PropertiesFile.loadFromProperties;

public class Task2Test {
    @Test
    public void testPropertiesNoFile() {
        //given
        Path testFilePath = Paths.get("");
        PropertiesFile.Class cls = new  PropertiesFile.Class<>();
        IllegalArgumentException a = Assertions.assertThrows(IllegalArgumentException.class,()-> {
            loadFromProperties(  cls, testFilePath);
        });
        Assertions.assertEquals("ОШИБКА: Проблемы с файлом!",a.getMessage());
    }
    @Test
    public void testPropertiesBadDate() {
        //given
        Path testFilePath = Paths.get("src/main/resources/configBadDate.properties");
        PropertiesFile.Class cls = new  PropertiesFile.Class<>();
        IllegalArgumentException a = Assertions.assertThrows(IllegalArgumentException.class,()-> {
            loadFromProperties(  cls, testFilePath);
        });
        Assertions.assertEquals("ОШИБКА: Проблемы с датой!",a.getMessage());
    }
    @Test
    public void testPropertiesNullElem() {
        //given
        Path testFilePath = Paths.get("src/main/resources/configNullElem.properties");
        PropertiesFile.Class cls = new  PropertiesFile.Class<>();
        IllegalArgumentException a = Assertions.assertThrows(IllegalArgumentException.class,()-> {
            loadFromProperties(  cls, testFilePath);
        });
        Assertions.assertEquals("ОШИБКА: Один из параметров пуст!",a.getMessage());
    }
}

