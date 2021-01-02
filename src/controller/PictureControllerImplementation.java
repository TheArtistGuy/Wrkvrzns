package controller;

import java.awt.*;

public class PictureControllerImplementation implements PictureController {


    @Override
    public Image createBitmap(Image image) {
        return image.getScaledInstance(50,50,Image.SCALE_SMOOTH);
    }
}
