/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.test.ui.administration.elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Represents the actions possible on the Administration Import Page.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class ImportAdministrationSectionPage extends ViewPage
{
    @FindBy(id = "packagelistcontainer")
    private WebElement packageList;

    @FindBy(id = "xwikiuploadfile")
    private WebElement uploadFileInputField;

    @FindBy(xpath = "//*[@id='attachform']//input[@type='submit']")
    private WebElement uploadFileSubmit;

    @FindBy(xpath = "//input[@value='Import']")
    private WebElement importPackageLink;

    public void gotoPage()
    {
        getUtil().gotoPage("XWiki", "XWikiPreferences", "import", "editor=globaladmin&section=Import");
    }

    public void attachPackage(URL file)
    {
        this.uploadFileInputField.sendKeys(file.getPath());
        this.uploadFileSubmit.submit();
    }

    public boolean isPackagePresent(String packageName)
    {
        return getPackageNames().contains(packageName);
    }

    /**
     * @since 2.6RC1
     */
    public List<String> getPackageNames()
    {
        List<String> names = new ArrayList<String>();
        for (WebElement element :
            getDriver().findElements(By.xpath("//div[@id='packagelistcontainer']//a[@class='package']")))
        {
            names.add(element.getText());
        }
        return names;
    }

    public void selectPackage(String packageName)
    {
        getDriver().findElement(By.linkText(packageName)).click();
        waitUntilElementIsVisible(By.id("packageDescription"));
    }

    public void deletePackage(String packageName)
    {
        List<WebElement> packages = this.packageList.findElements(By.xpath("//ul[@class='xlist']/li/div"));
        for (WebElement pack : packages) {
            try {
                pack.findElement(By.partialLinkText(packageName));
                makeConfirmDialogSilent(true); // temporary, see BaseElement#makeConfirmDialogSilent
                pack.findElement(By.xpath("//div/span/a[@class='deletelink']")).click();
                return;
            } catch (NoSuchElementException e) {
                // Not the right package. Try again.
            }
        }
        throw new NoSuchElementException(packageName);
    }

    public void importPackage()
    {
        // Click submit
        this.importPackageLink.click();
        // Wait for the "Import successful message"
        this.waitUntilElementIsVisible(By.xpath("//div[@id='packagecontainer']/div[@class='infomessage']"));
    }

    public ViewPage clickImportedPage(String pageName)
    {
        this.waitUntilElementIsVisible(By.linkText(pageName));
        getDriver().findElement(By.linkText(pageName)).click();
        return new ViewPage();
    }

    public void selectReplaceHistoryOption()
    {
        getDriver().findElement(By.xpath("//input[@name='historyStrategy' and @value='replace']")).click();
    }
}
