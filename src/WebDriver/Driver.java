package WebDriver;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class Driver extends HtmlUnitDriver {

    public Driver(){
        super(true);
    }

    @Override
    public WebElement findElement(By by) {
        return (new WebDriverWait(this, 60)).until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public WebElement findElement(By by, int time) {
        return (new WebDriverWait(this, time)).until(ExpectedConditions.presenceOfElementLocated(by));
    }
}