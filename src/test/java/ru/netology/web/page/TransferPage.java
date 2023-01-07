package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage {

    private SelenideElement heading = $("[data-test-id=dashboard]");
    private SelenideElement amountInput = $("[data-test-id=amount] input");
    private SelenideElement fromInput = $("[data-test-id=from] input");
    private SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private SelenideElement errorMassage = $("[data-test-id='error-massage']");
    private SelenideElement cancelButton = $("[data-test-id='action-cancel']");

    public TransferPage() {
        heading.shouldBe(Condition.visible);
    }

    public TransferPage makeTransfer(String amount, DataHelper.CardInfo cardInfo) {
        amountInput.setValue(amount);
        fromInput.setValue(cardInfo.getCardNumber());
        return this;
    }

    public DashboardPage validTransfer(String amount, DataHelper.CardInfo cardInfo) {
        makeTransfer(amount, cardInfo);
        transferButton.click();
        return new DashboardPage();
    }

    public TransferPage invalidTransfer(String amount, DataHelper.CardInfo cardInfo) {
        makeTransfer(amount, cardInfo);
        transferButton.click();
        return this;
    }

    public DashboardPage cancelTransfer() {
        cancelButton.click();
        return new DashboardPage();
    }

    public void errorMassage(String expectedText) {
        errorMassage.shouldHave(Condition.exactText(expectedText)).shouldBe(Condition.visible);
    }
}