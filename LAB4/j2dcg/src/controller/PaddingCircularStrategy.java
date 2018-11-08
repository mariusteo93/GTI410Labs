package controller;
import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;
public class PaddingCircularStrategy extends PaddingStrategy {
    @Override
    public Pixel pixelAt(ImageX image, int x, int y) {
        x = x % image.getImageWidth();
        y = y % image.getImageHeight();
        return new Pixel(image.getPixelInt(x,y));
    }
    @Override
    public PixelDouble pixelAt(ImageDouble image, int x, int y) {
        int xx = (((x % image.getImageWidth()) + image.getImageWidth()) % image.getImageWidth());
        int yy = (((y % image.getImageHeight()) + image.getImageHeight()) % image.getImageHeight());
        return new PixelDouble(image.getPixel(xx,yy));
    }
}