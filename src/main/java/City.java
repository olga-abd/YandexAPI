public class City {
    public double distance;
    public String code;
    public String title;
    public double lat;
    public double lng;

    public void print() {
        System.out.println();
        System.out.println("Ближайший город: " + title + " (широта " + lat + ", долгота " + lng + ", " + code + ")");
    }
}
