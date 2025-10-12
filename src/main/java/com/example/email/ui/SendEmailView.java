package com.example.email.ui;

import com.example.base.ui.MainLayout;
import com.example.email.EmailService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "send-email", layout = MainLayout.class)
@PageTitle("Enviar Email")
public class SendEmailView extends VerticalLayout {

    private final EmailService emailService;

    @Autowired
    public SendEmailView(EmailService emailService) {
        this.emailService = emailService;

        setPadding(true);
        setSpacing(true);
        setSizeFull();

        add(new H2("Enviar Email (simulado)"));

        TextField toField = new TextField("Para (destinatário)");
        TextField subjectField = new TextField("Assunto");
        TextArea bodyField = new TextArea("Corpo do email");

        toField.setValue("user@example.com");
        subjectField.setValue("Confirmação de Ação");
        bodyField.setValue("Este é um email de confirmação simulado que regista a operação.");

        FormLayout form = new FormLayout();
        form.add(toField, subjectField, bodyField);
        add(form);

        // ✅ Cria o botão ANTES de usar dentro da lambda
        Button sendButton = new Button("Enviar Email");
        sendButton.addClickListener(event -> {
            sendButton.setEnabled(false);
            emailService.sendEmail(toField.getValue(), subjectField.getValue(), bodyField.getValue());
            Notification.show("Pedido de envio registado. Receberá confirmação (simulada).",
                    4000, Notification.Position.TOP_CENTER);
            sendButton.setEnabled(true);
        });

        add(sendButton);
    }
}
