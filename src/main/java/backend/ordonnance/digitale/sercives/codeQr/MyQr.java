package backend.ordonnance.digitale.sercives.codeQr;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class MyQr {
    public  void createQR(String data, String path, String charset, Map hashMap,int height,int width) throws WriterException , IOException {
        BitMatrix matrix=new MultiFormatWriter().encode(
                new String(data.getBytes(charset),charset),
                BarcodeFormat.QR_CODE,width,height
        );
        MatrixToImageWriter.writeToFile(
                matrix,
                path.substring(path.lastIndexOf('.')+1),
                new File(path));

    }

    public String readQR(BufferedImage bufferedImage, String charset, Map hashMap) throws NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(
                new HybridBinarizer(
                        new BufferedImageLuminanceSource(bufferedImage)
                )
        );
        Result result = new MultiFormatReader().decode(binaryBitmap);
        return result.getText();
    }


}
