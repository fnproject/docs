package com.fnproject.fn.examples;

import com.fnproject.fn.api.RuntimeContext;
import com.fnproject.fn.api.httpgateway.HTTPGatewayContext;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import java.io.ByteArrayOutputStream;

public class QRGen {
    private final String defaultFormat;

    public QRGen(RuntimeContext ctx) {
        defaultFormat = ctx.getConfigurationByKey("FORMAT").orElse("png");
    }

    public byte[] create(HTTPGatewayContext hctx) {
        ImageType type = getFormat(hctx.getQueryParameters().get("format").orElse(defaultFormat));
        System.err.println("Default format: " + type.toString());
        String contents = hctx.getQueryParameters().get("contents").orElseThrow(() -> new RuntimeException("Contents must be provided to the QR code"));

        ByteArrayOutputStream stream = QRCode.from(contents).to(type).stream();
        System.err.println("Generated QR Code for contents: " + contents);

        hctx.setResponseHeader("Content-Type", getMimeType(type));
        return stream.toByteArray();
    }

    private ImageType getFormat(String extension) {
        switch (extension.toLowerCase()) {
            case "png":
                return ImageType.PNG;
            case "jpg":
            case "jpeg":
                return ImageType.JPG;
            case "gif":
                return ImageType.GIF;
            case "bmp":
                return ImageType.BMP;
            default:
                throw new RuntimeException(String.format("Cannot use the specified format %s, must be one of png, jpg, gif, bmp", extension));
        }
    }

    private String getMimeType(ImageType type) {
        switch (type) {
            case JPG:
                return "image/jpeg";
            case GIF:
                return "image/gif";
            case PNG:
                return "image/png";
            case BMP:
                return "image/bmp";
            default:
                throw new RuntimeException("Invalid ImageType: " + type);
        }
    }
}
