package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {

    private SelenideElement codeField = $("[data-test-id=code] input");
    private SelenideElement verifyButton = $("[data-test-id='action-verify']");
    private SelenideElement errorNotification = $("[data-test-id='error-notification']");

    public VerificationPage() {
        codeField.shouldBe(Condition.visible);
    }

    public void verification(DataHelper.VerificationCode code) {
        codeField.setValue(code.getCode());
        verifyButton.click();
    }

    public DashboardPage validVerify(DataHelper.VerificationCode code) {
        verification(code);
        return new DashboardPage();
    }

    public VerificationPage invalidVerify(DataHelper.VerificationCode code) {
        verification(code);
        return this;
    }

    public void errorNotification(String expectedText) {
        errorNotification.shouldHave(Condition.exactText(expectedText)).shouldBe(Condition.visible);
    }
}