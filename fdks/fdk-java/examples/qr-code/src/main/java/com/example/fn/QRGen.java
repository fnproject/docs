package com.example.fn;

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
        // If format or contents is empty, set default to png and Hello World
        ImageType type = getFormat(hctx.getQueryParameters().get("format").orElse(defaultFormat));
        System.err.println("Format set to: " + type.toString());
        
        String contents = hctx.getQueryParameters().get("contents").orElse("QRCode Hello World!");
        System.err.println("QR code generated from contents: " + contents);
            
        ByteArrayOutputStream stream = QRCode.from(contents).to(type).stream();

        hctx.setResponseHeader("Content-Type", getMimeType(type));
        return stream.toByteArray();
    }

    private ImageType getFormat(final String extension) {
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
                return ImageType.PNG;
        }
    }

    private String getMimeType(final ImageType type) {
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
                return "image/png";
        }
    }
}
