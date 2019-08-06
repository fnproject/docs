package com.example.fn;

import com.fnproject.fn.testing.FnTestingRule;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import org.junit.Rule;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class QRGenTest {
    @Rule
    public FnTestingRule fn = FnTestingRule.createDefault();

    @Test
    public void textHelloWorld() {
        String content = "hello world";
        String decodedImage = null;
        fn.givenEvent()
          .withHeader("Fn-Http-Request-Url", "http://www.example.com/qr?contents=hello+world&format=png")
          .withHeader("Fn-Http-Method","GET")
          .enqueue();
        fn.thenRun(QRGen.class, "create");

        try {
        	decodedImage = decode(fn.getOnlyResult().getBodyAsBytes());
        } catch(Exception e) {
        	System.err.println("Failed to decode image: " + e.getStackTrace());
        }
        
        assertEquals(content, decodedImage);
    }

    @Test
    public void phoneNumber() {
        String telephoneNumber = "tel:0-12345-67890";
        String decodedImage = null;
        fn.givenEvent()
          .withHeader("Fn-Http-Request-Url", "http://www.example.com/qr?contents=tel:0-12345-67890")
          .withHeader("Fn-Http-Method","GET")
          .enqueue();
        fn.thenRun(QRGen.class, "create");

        try {
        	decodedImage = decode(fn.getOnlyResult().getBodyAsBytes());
        } catch(Exception e) {
        	System.err.println("Failed to decode image: " + e.getStackTrace());
        }
        
        assertEquals(telephoneNumber, decodedImage);
        
    }

    @Test
    public void formatConfigurationIsUsedIfNoFormatIsProvided() {
        String contents = "hello world";
        String decodedImage = null;
        fn.setConfig("FORMAT", "jpg");
        fn.givenEvent()
          .withHeader("Fn-Http-Request-Url", "http://www.example.com/qr?contents=hello+world")
          .withHeader("Fn-Http-Method","GET")
          .enqueue();
        fn.thenRun(QRGen.class, "create");

        try {
        	decodedImage = decode(fn.getOnlyResult().getBodyAsBytes());
        } catch(Exception e) {
        	System.err.println("Failed to decode image: " + e.getStackTrace());
        }
        
        assertEquals(contents, decodedImage);
        
    }

    private String decode(final byte[] imageBytes) throws IOException, NotFoundException, ChecksumException, FormatException {
        BinaryBitmap bitmap = readToBitmap(imageBytes);
        return new QRCodeReader().decode(bitmap).getText();
    }

    private BinaryBitmap readToBitmap(final byte[] imageBytes) throws IOException {
        BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        BufferedImageLuminanceSource luminanceSource = new BufferedImageLuminanceSource(inputImage);
        HybridBinarizer binarizer = new HybridBinarizer(luminanceSource);
        return new BinaryBitmap(binarizer);
    }
}
