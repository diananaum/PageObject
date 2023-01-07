package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {
    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    void shouldTransferMoneyToFirstCard() {
        var authInfo = DataHelper.getAuthInfo();

        var dashboardPage = new LoginPage()
                .validLogin(authInfo)
                .validVerify(DataHelper.getVerificationCode());

        var firstCardInfo = DataHelper.getFirstCardNumber();
        var secondCardInfo = DataHelper.getSecondCardNumber();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = DataHelper.validAmount(secondCardBalance);

        dashboardPage
                .topUpCard(firstCardInfo)
                .validTransfer(String.valueOf(amount), secondCardInfo);

        var expectedFirstCardBalance = firstCardBalance + amount;
        var expectedSecondCardBalance = secondCardBalance - amount;

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldTransferMoneyToSecondCard() {
        var authInfo = DataHelper.getAuthInfo();

        var dashboardPage = new LoginPage()
                .validLogin(authInfo)
                .validVerify(DataHelper.getVerificationCode());

        var firstCardInfo = DataHelper.getFirstCardNumber();
        var secondCardInfo = DataHelper.getSecondCardNumber();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = DataHelper.validAmount(firstCardBalance);

        dashboardPage
                .topUpCard(secondCardInfo)
                .validTransfer(String.valueOf(amount), firstCardInfo);

        var expectedFirstCardBalance = firstCardBalance - amount;
        var expectedSecondCardBalance = secondCardBalance + amount;

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferMoneyIfInvalidAmount() {
        var authInfo = DataHelper.getAuthInfo();

        var dashboardPage = new LoginPage()
                .validLogin(authInfo)
                .validVerify(DataHelper.getVerificationCode());

        var firstCardInfo = DataHelper.getFirstCardNumber();
        var secondCardInfo = DataHelper.getSecondCardNumber();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = DataHelper.invalidAmount(firstCardBalance);

        dashboardPage
                .topUpCard(secondCardInfo)
                .invalidTransfer(String.valueOf(amount), firstCardInfo)
                .errorMassage("Недостаточно средств");

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

        assertEquals(firstCardBalance, actualFirstCardBalance);
        assertEquals(secondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldHaveEmptyInputsAfterCancel() {
        var authInfo = DataHelper.getAuthInfo();

        var dashboardPage = new LoginPage()
                .validLogin(authInfo)
                .validVerify(DataHelper.getVerificationCode());

        var firstCardInfo = DataHelper.getFirstCardNumber();
        var secondCardInfo = DataHelper.getSecondCardNumber();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = DataHelper.validAmount(firstCardBalance);

        dashboardPage
                .topUpCard(firstCardInfo)
                .makeTransfer(String.valueOf(amount), secondCardInfo)
                .cancelTransfer()
                .topUpCard(firstCardInfo)
                .validTransfer(String.valueOf(amount), secondCardInfo);

        var expectedFirstCardBalance = firstCardBalance + amount;
        var expectedSecondCardBalance = secondCardBalance - amount;

        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCardInfo);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldShowErrorNotificationIfTransferMoneySameCard() {
        var authInfo = DataHelper.getAuthInfo();

        var dashboardPage = new LoginPage()
                .validLogin(authInfo)
                .validVerify(DataHelper.getVerificationCode());

        var firstCardInfo = DataHelper.getFirstCardNumber();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var amount = DataHelper.validAmount(firstCardBalance);

        dashboardPage
                .topUpCard(firstCardInfo)
                .invalidTransfer(String.valueOf(amount), firstCardInfo)
                .errorMassage("Номера карт совпадают");
    }
}