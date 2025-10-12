package com.example.currency_exchange.ui;

import com.example.currency_exchange.Exchange;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.List;

/**
 * View Vaadin para conversão automática de moedas usando JavaMoney/Moneta.
 * O usuário seleciona as moedas e insere o valor. A taxa é obtida automaticamente.
 */
@Route("exchange")
@PageTitle("Currency Conversion")
@Menu(order = 1, icon = "vaadin:money", title = "Currency Exchange")
public class ExchangeView extends VerticalLayout {
    // ComboBox para seleção da moeda de origem
    final ComboBox<String> fromCurrency = new ComboBox<>("Moeda de origem");
    // ComboBox para seleção da moeda de destino
    final ComboBox<String> toCurrency = new ComboBox<>("Moeda de destino");
    // Campo para o valor a converter
    final TextField amount = new TextField("Valor a converter");
    // Botão para acionar a conversão
    final Button exchangeBtn = new Button("Converter");
    // Campo somente leitura para mostrar o resultado
    final TextField result = new TextField("Resultado");

    public ExchangeView() {
        // Lista fixa de moedas suportadas (pode ser expandida)
        List<String> currencies = Arrays.asList("USD", "EUR", "BRL", "GBP", "JPY", "CNY", "AUD", "CAD", "CHF", "SEK", "NZD");
        fromCurrency.setItems(currencies);
        toCurrency.setItems(currencies);
        fromCurrency.setPlaceholder("Escolha a moeda de origem");
        toCurrency.setPlaceholder("Escolha a moeda de destino");
        amount.setPlaceholder("100");
        result.setReadOnly(true);

        // Listener do botão de conversão
        exchangeBtn.addClickListener(e -> {
            String from = fromCurrency.getValue();
            String to = toCurrency.getValue();
            String amountStr = amount.getValue().trim();

            // Validação dos campos obrigatórios
            if (from == null || to == null) {
                Notification.show("Selecione ambas as moedas.");
                return;
            }
            if (from.equals(to)) {
                Notification.show("Escolha moedas diferentes.");
                return;
            }
            if (amountStr.isEmpty()) {
                Notification.show("Preencha o valor a converter.");
                return;
            }
            try {
                double valor = Double.parseDouble(amountStr);
                if (valor <= 0) {
                    Notification.show("O valor deve ser positivo.");
                    return;
                }
                // Realiza a conversão usando a classe Exchange (Moneta)
                Exchange exchange = new Exchange(from, to);
                double convertido = exchange.exchange(valor);
                result.setValue(valor + " " + from + " = " + String.format("%.2f", convertido) + " " + to);
            } catch (javax.money.UnknownCurrencyException ex) {
                Notification.show("Código de moeda inválido ou não suportado.");
            } catch (RuntimeException ex) {
                // Trata erro de taxa de câmbio não disponível
                if (ex.getMessage() != null && ex.getMessage().contains("taxa de câmbio")) {
                    Notification.show("Não foi possível obter taxa de câmbio para as moedas selecionadas.");
                } else {
                    Notification.show("Erro ao obter taxa de câmbio: " + ex.getMessage());
                }
            } catch (Exception ex) {
                Notification.show("Erro ao obter taxa de câmbio: " + ex.getMessage());
            }
        });

        // Adiciona os componentes à view
        add(
            fromCurrency,
            toCurrency,
            amount,
            exchangeBtn,
            result
        );
    }
}
