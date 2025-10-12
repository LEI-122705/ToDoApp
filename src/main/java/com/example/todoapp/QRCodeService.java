package com.example.todoapp;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {

    private static final int QR_SIZE = 200;

    /**
     * Gera um QR Code para o texto fornecido e retorna a imagem como um array de bytes (PNG).
     * @param text O texto a ser codificado no QR Code (URL, ID, etc.)
     * @return Um array de bytes contendo a imagem PNG do QR Code, ou null em caso de erro.
     */
    public byte[] generateQRCodeImage(String text) {
        try {
            // Configurações para o QR Code
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Alta correção de erro

            // Gerar a matriz de bits
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    QR_SIZE,
                    QR_SIZE,
                    hints
            );

            // Converter a matriz de bits em imagem (array de bytes)
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * (Opcional - para demonstração) Gera o QR Code e salva em um arquivo.
     * @param text O texto a ser codificado.
     * @param filePath O caminho completo do arquivo de saída.
     */
    public void generateAndSaveQRCode(String text, String filePath) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix matrix = new MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    QR_SIZE,
                    QR_SIZE,
                    hints
            );

            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Nota: A função de 'leitura' (decodificação) deve ser implementada
    // usando a classe BinaryBitmap e MultiFormatReader, geralmente
    // com uma imagem de entrada (FileInputStream ou similar).
}