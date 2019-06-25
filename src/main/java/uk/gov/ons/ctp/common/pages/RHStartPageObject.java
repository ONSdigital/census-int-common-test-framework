package uk.gov.ons.ctp.common.pages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RHStartPageObject extends PageObject {

  @FindBy(css = "#iac1")
  private WebElement uacTextBox1;

  @FindBy(css = "#iac2")
  private WebElement uacTextBox2;

  @FindBy(css = "#iac3")
  private WebElement uacTextBox3;

  @FindBy(css = "#iac4")
  private WebElement uacTextBox4;
  
//  @FindBy(how = How.ID, using = "iac1")
  
  
  private WebElement startNowButton;

  public RHStartPageObject(WebDriver driver) {
	  super(driver);
	  
  }

  public void clickUacBox1() {
    uacTextBox1.click();
  }

  public void addTextToUac1(String txtToAdd) {
    uacTextBox1.sendKeys(txtToAdd);
  }

  public void clickUacBox2() {
    uacTextBox2.click();
  }

  public void addTextToUac2(String txtToAdd) {
    uacTextBox2.sendKeys(txtToAdd);
  }

  public void clickUacBox3() {
    uacTextBox3.click();
  }

  public void addTextToUac3(String txtToAdd) {
    uacTextBox3.sendKeys(txtToAdd);
  }

  public void clickUacBox4() {
    uacTextBox4.click();
  }

  public void addTextToUac4(String txtToAdd) {
    uacTextBox4.sendKeys(txtToAdd);
  }

  public void enterUac(String uac1, String uac2, String uac3, String uac4) {
    clickUacBox1();
    addTextToUac1(uac1);
    clickUacBox2();
    addTextToUac2(uac2);
    clickUacBox3();
    addTextToUac3(uac3);
    clickUacBox4();
    addTextToUac4(uac4);
  }
}
