package com.example.todoapp;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;

@Route("qrcode") // Rota de acesso: http://localhost:8080/qrcode
public class QRCodeView extends VerticalLayout {

    private final QRCodeService qrCodeService;
    private final TextField inputField = new TextField("Informação para o QR Code (URL/ID/Contacto)");
    private final Image qrCodeImage = new Image();

    // Injeção de dependência no construtor (Spring Boot)
    public QRCodeView(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;

        // 1. Componentes para Geração
        inputField.setValue("https://www.iscte-iul.pt/");
        Button generateButton = new Button("Gerar QR Code", event -> generateQRCode());

        // Configurar a imagem (invisível por padrão)
        qrCodeImage.setWidth("200px");
        qrCodeImage.setHeight("200px");
        qrCodeImage.setVisible(false);

        // Layout
        add(new Div("--- Gerar QR Code ---"), inputField, generateButton, qrCodeImage);

        // 2. (Desafio) Componentes para Leitura - Requer um file upload
        // (Seria necessário um componente 'Upload' e a lógica de leitura no QRCodeService)
        add(new Div("--- Ler QR Code ---"));

        setPadding(true);
        setSpacing(true);
    }

    private void generateQRCode() {
        String data = inputField.getValue();
        if (data == null || data.trim().isEmpty()) {
            Notification.show("A informação não pode estar vazia.", 3000, Notification.Position.MIDDLE);
            return;
        }

        byte[] qrCodeBytes = qrCodeService.generateQRCodeImage(data);

        if (qrCodeBytes != null) {
            // Cria um recurso de stream para a imagem (necessário para o componente Image do Vaadin)
            StreamResource resource = new StreamResource("qrcode.png",
                    () -> new ByteArrayInputStream(qrCodeBytes)
            );

            qrCodeImage.setSrc(resource);
            qrCodeImage.setAlt("QR Code para: " + data);
            qrCodeImage.setVisible(true);
            Notification.show("QR Code gerado com sucesso!", 3000, Notification.Position.BOTTOM_END);
        } else {
            qrCodeImage.setVisible(false);
            Notification.show("Erro ao gerar o QR Code. Consulte o log da aplicação.", 5000, Notification.Position.MIDDLE);
        }
    }
}