import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Main {

    private static int imageWidth;
    private static int imageHeight;
    //41.996532 21.431313
    private static double lat = 41.996480;
    private static double lng = 21.431325;

    private static int zoom = 16; //TODO maybe not 16

    private static List<LatLngModel> latLngModels = new ArrayList<LatLngModel>();


    public static void main(String[] args) {

        readFile("/home/martin/IdeaProjects/LatLngImage/src/main/c1_26_9_2018_16_31_59_out.png");
    }

    private static void readFile(String path) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();

        BufferedImage bufferedImage = null;
        try {
            File file = new File(path);
            bufferedImage = ImageIO.read(file);
            imageWidth = bufferedImage.getWidth();
            imageHeight = bufferedImage.getHeight();

            hashMap.put("time", getTimeFromFile(file));
            for (int i = 0; i < imageWidth; i++) {
                for (int y = 0; y < imageHeight; y++) {
                    getLatLong(i, y, bufferedImage);
                }
            }
            hashMap.put("latlnglist", latLngModels);
            FileWriter fileWriter = new FileWriter("/home/martin/IdeaProjects/LatLngImage/src/main/java/latlngtime.json");
            fileWriter.write(new Gson().toJson(hashMap));
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getLatLong(int x, int y, BufferedImage bufferedImage) {


        double parallelMultiplier = Math.cos(lat * Math.PI / 180);

        double degreesPerPixelX = 360 / Math.pow(2, zoom + 8);
        double degreesPerPixelY = 360 / Math.pow(2, zoom + 8) * parallelMultiplier;
        double pointLat = lat - degreesPerPixelY * (y - (double) imageHeight / 2);
        double pointLng = lng + degreesPerPixelX * (x - (double) imageWidth / 2);

        System.out.println(pointLat);
        System.out.println(pointLng);


        Color colorForPixel = new Color(bufferedImage.getRGB(x, y));

        int color = colorForPixel.getRGB();

        int red = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue = color & 0x000000ff;

        if (red != 255 || green != 255 || blue != 255) {
            if (red >= 127 && green >= 127) {
                latLngModels.add(new LatLngModel(pointLat, pointLng, "YELLOW"));
            } else if (green > 127) {
                latLngModels.add(new LatLngModel(pointLat, pointLng, "GREEN"));
            } else if (red > 127) {
                latLngModels.add(new LatLngModel(pointLat, pointLng, "RED"));
            }
        }

        System.out.println(red + " " + green + " " + blue);

    }

    private static String getTimeFromFile(File file) {

        String formattedDate = null;

        StringBuilder stringBuilder = new StringBuilder();

        SimpleDateFormat originalFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy HH-mm-ss");

        String[] digits = file.getName().substring(3, file.getName().length() - 8).replaceAll("[^\\d_]", "").split("");

        int digitsCount = 0;
        while (digitsCount <= digits.length - 1) {
            if (digitsCount + 1 <= digits.length - 1) {
                if (digits[digitsCount].matches("\\d+") && !digits[digitsCount + 1].matches("\\d+")) {
                    if (digitsCount - 1 >= 0 && digits[digitsCount - 1].matches("\\d+")) {
                        stringBuilder.append(digits[digitsCount]);
                    } else {
                        stringBuilder.append("0").append(digits[digitsCount]);
                    }
                } else {
                    stringBuilder.append(digits[digitsCount]);
                }
            } else {
                stringBuilder.append(digits[digitsCount]);
            }
            digitsCount++;
        }

        try {
            Date date = originalFormat.parse(stringBuilder.toString().replaceAll("_", ""));
            formattedDate = newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

}
