public class LatLngModel {

    private double lat;
    private double lng;

    private String color;


    public LatLngModel(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public LatLngModel(double lat, double lng, String color) {
        this.lat = lat;
        this.lng = lng;
        this.color = color;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getColor() {
        return color;
    }
}
