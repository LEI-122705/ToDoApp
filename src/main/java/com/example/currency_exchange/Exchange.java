package com.example.currency_exchange;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;
import javax.money.convert.ExchangeRateProvider;
import java.util.Set;

public class Exchange {
    private final String currencyFrom;
    private final String currencyTo;

    public Exchange(String currencyFrom, String currencyTo) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
    }

    public double exchange(double amount) {
        CurrencyUnit from = Monetary.getCurrency(currencyFrom);
        CurrencyUnit to = Monetary.getCurrency(currencyTo);

        MonetaryAmount money = Monetary.getDefaultAmountFactory()
                .setCurrency(from)
                .setNumber(amount)
                .create();
        try {
            // Usa explicictamente o provedor "ECB"
            ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider("ECB");
            CurrencyConversion conversion = provider.getCurrencyConversion(to);

            MonetaryAmount converted = money.with(conversion);
            return converted.getNumber().doubleValue();
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível obter taxa de câmbio para as moedas selecionadas.", e);
        }
    }
}
