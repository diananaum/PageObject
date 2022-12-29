package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.TransferPage;
import ru.netology.web.page.VerificationPage;

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
        new LoginPage()
                .validLogin(authInfo)
                .validVerify(DataHelper.getVerificationCode());
        var firstCardInfo = DataHelper.getFirstCardNumber();
        var secondCardInfo = DataHelper.getSecondCardNumber();
        var firstCardBalance = new DashboardPage().getCardBalance(firstCardInfo);
        var secondCardBalance = new DashboardPage().getCardBalance(secondCardInfo);
        var amount = DataHelper.validAmount(secondCardBalance);

        new DashboardPage()
                .topUpCard(firstCardInfo);
        new TransferPage()
                .validTransfer(String.valueOf(amount), secondCardInfo);

        var expectedFirstCardBalance = firstCardBalance + amount;
        var expectedSecondCardBalance = secondCardBalance - amount;

        var actualFirstCardBalance = new DashboardPage().getCardBalance(firstCardInfo);
        var actualSecondCardBalance = new DashboardPage().getCardBalance(secondCardInfo);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldTransferMoneyToSecondCard() {
        var authInfo = DataHelper.getAuthInfo();
        new LoginPage()
                .validLogin(authInfo);
        new VerificationPage()
                .validVerify(DataHelper.getVerificationCode());

        var firstCardInfo = DataHelper.getFirstCardNumber();
        var secondCardInfo = DataHelper.getSecondCardNumber();
        var firstCardBalance = new DashboardPage().getCardBalance(firstCardInfo);
        var secondCardBalance = new DashboardPage().getCardBalance(secondCardInfo);
        var amount = DataHelper.validAmount(firstCardBalance);

        new DashboardPage()
                .topUpCard(secondCardInfo);
        new TransferPage()
                .validTransfer(String.valueOf(amount), firstCardInfo);

        var expectedFirstCardBalance = firstCardBalance - amount;
        var expectedSecondCardBalance = secondCardBalance + amount;

        var actualFirstCardBalance = new DashboardPage().getCardBalance(firstCardInfo);
        var actualSecondCardBalance = new DashboardPage().getCardBalance(secondCardInfo);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldNotTransferMoneyIfInvalidAmount() {
        var authInfo = DataHelper.getAuthInfo();
        new LoginPage()
                .validLogin(authInfo);
        new VerificationPage()
                .validVerify(DataHelper.getVerificationCode());

        var firstCardInfo = DataHelper.getFirstCardNumber();
        var secondCardInfo = DataHelper.getSecondCardNumber();
        var firstCardBalance = new DashboardPage().getCardBalance(firstCardInfo);
        var secondCardBalance = new DashboardPage().getCardBalance(secondCardInfo);
        var amount = DataHelper.invalidAmount(firstCardBalance);

        new DashboardPage()
                .topUpCard(secondCardInfo);
        new TransferPage()
                .validTransfer(String.valueOf(amount), firstCardInfo);
        new TransferPage().errorMassage("Недостаточно средств");

        var actualFirstCardBalance = new DashboardPage().getCardBalance(firstCardInfo);
        var actualSecondCardBalance = new DashboardPage().getCardBalance(secondCardInfo);

        assertEquals(firstCardBalance, actualFirstCardBalance);
        assertEquals(secondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldHaveEmptyInputsAfterCancel() {
        var authInfo = DataHelper.getAuthInfo();

        new LoginPage()
                .validLogin(authInfo);
        new VerificationPage()
                .validVerify(DataHelper.getVerificationCode());

        var firstCardInfo = DataHelper.getFirstCardNumber();
        var secondCardInfo = DataHelper.getSecondCardNumber();
        var firstCardBalance = new DashboardPage().getCardBalance(firstCardInfo);
        var secondCardBalance = new DashboardPage().getCardBalance(secondCardInfo);
        var amount = DataHelper.validAmount(firstCardBalance);

        new DashboardPage()
                .topUpCard(firstCardInfo)
                .makeTransfer(String.valueOf(amount), secondCardInfo);
        new TransferPage()
                .cancelTransfer();
        new DashboardPage()
                .topUpCard(firstCardInfo)
                .validTransfer(String.valueOf(amount), secondCardInfo);

        var expectedFirstCardBalance = firstCardBalance + amount;
        var expectedSecondCardBalance = secondCardBalance - amount;

        var actualFirstCardBalance = new DashboardPage().getCardBalance(firstCardInfo);
        var actualSecondCardBalance = new DashboardPage().getCardBalance(secondCardInfo);

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    void shouldShowErrorNotificationIfTransferMoneySameCard() {
        var authInfo = DataHelper.getAuthInfo();

        new LoginPage()
                .validLogin(authInfo);
        new VerificationPage()
                .validVerify(DataHelper.getVerificationCode());

        var firstCardInfo = DataHelper.getFirstCardNumber();
        var firstCardBalance = new DashboardPage().getCardBalance(firstCardInfo);
        var amount = DataHelper.validAmount(firstCardBalance);

        new DashboardPage()
                .topUpCard(firstCardInfo)
                .validTransfer(String.valueOf(amount), firstCardInfo);
        new TransferPage().errorMassage("Номера карт совпадают");
    }
}