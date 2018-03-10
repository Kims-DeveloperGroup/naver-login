package com.devoo.naverlogin;

import com.devoo.naverlogin.exception.NaverLoginFailException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NaverClient extends WebDriverUtilClient {
    static final String LOGIN_USERINFO_ELEMENT_CLASSNAME = "section_minime";

    private final String NAVER_HOME_URL = "https://www.naver.com/";
    private final String LOGIN_BUTTON_SELECTOR = "#frmNIDLogin fieldset .btn_login input";
    private String LOGIN_FORM_ID = "frmNIDLogin";
    private WebDriver webDriver;

    public NaverClient() {
        super(new ChromeDriver());
        this.webDriver = super.getWebDriver();
    }

    public NaverClient(WebDriver webDriver) {
        super(webDriver);
        this.webDriver = super.getWebDriver();
    }

    public NaverClient tryLogin(String id, String password) throws NaverLoginFailException {
        this.webDriver.get(NAVER_HOME_URL);
        new WebDriverWait(this.webDriver, 5)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id(LOGIN_FORM_ID)));
        this.webDriver.findElement(By.id("id")).sendKeys(id);
        this.webDriver.findElement(By.id("pw")).sendKeys(password);
        this.webDriver.findElement(By.cssSelector(LOGIN_BUTTON_SELECTOR)).click();
        if (!isLoginSucceeded()) {
            throw new NaverLoginFailException();
        }
        return this;
    }

    public boolean isLoginSucceeded() {
        try {
            String classNameOfLoginElement =
                    webDriver.findElement(By.id("account")).getAttribute("class");
            if (classNameOfLoginElement.equals(LOGIN_USERINFO_ELEMENT_CLASSNAME)) {
                return true;
            }
        }catch (NoSuchElementException e) {
            return false;
        }
        return false;
    }
}