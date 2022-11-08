package ru.kortunov.wordstress.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.kortunov.wordstress.dto.TelegramCommand;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrfoDictionaryTest {

    @Autowired
    private Dictionary dictionary;

    @Test
    public void findWordEge() {
        var actual = dictionary.search("дозировать", TelegramCommand.ORFO_EGE);
        Assert.assertTrue(actual.isPresent());
        Assert.assertTrue(actual.get().contains("дозИровать"));
    }

    @Test
    public void findWordAll() {
        var actual = dictionary.search("абвер", TelegramCommand.ORFO_ALL);
        Assert.assertTrue(actual.isPresent());
        Assert.assertTrue(actual.get().contains("Абвер"));
    }

    @Test
    public void findMissingWord() {
        var actualEge = dictionary.search("miss", TelegramCommand.ORFO_EGE);
        var actualAll = dictionary.search("miss", TelegramCommand.ORFO_ALL);

        Assert.assertTrue(actualEge.isEmpty());
        Assert.assertTrue(actualAll.isEmpty());
    }
}
