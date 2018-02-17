package com.devoo.naverlogin;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LoginTest {

    private final String ID = "CREDENTIAL";
    private final String PASSWORD = "CREDENTIAL";

    @Test
    public void shouldBeLoginUserInfoSectionFound_whenLoginSucceeds() {
        //Given
        Login login = new Login();
        String loginUserInfoElementClassName = "section_minime";

        //When
        WebDriver webDriver = login.tryLogin(ID, PASSWORD);
        List<WebElement> spans = webDriver.findElements(By.tagName("span"));

        //Then
        assertEquals(webDriver.findElement(By.id("account"))
                .getAttribute("class"), loginUserInfoElementClassName);
    }
}