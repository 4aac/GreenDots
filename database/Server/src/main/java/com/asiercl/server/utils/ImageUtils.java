package com.asiercl.server.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageUtils {

    public static byte[] redimensionarImagen(byte[] imagenBytes, int ancho, int alto) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imagenBytes);
        BufferedImage imagenOriginal = ImageIO.read(bais);

        Image imagenEscalada = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        BufferedImage imagenRedimensionada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagenRedimensionada.createGraphics();
        g2d.drawImage(imagenEscalada, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagenRedimensionada, "jpg", baos);
        return baos.toByteArray();
    }
}
