package uk.gov.ons.ctp.common.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageObject {
	
	private WebDriver driver;
	
	public PageObject(WebDriver driver) {
	    this.driver = driver;
	    PageFactory.initElements(driver, this);
	  }
}
