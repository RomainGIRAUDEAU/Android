package ihm.si3.polytech.projetnote.utility;

public class Batiment {
    private double Latitude1,latitude2,longitude1,longitude2;
    private String name;

    public double getLatitude1() {
        return Latitude1;
    }

    public double getLatitude2() {
        return latitude2;
    }

    public double getLongitude1() {
        return longitude1;
    }

    public double getLongitude2() {
        return longitude2;
    }

    public String getName() {
        return name;
    }

    public void setLatitude1(double latitude1) {
        Latitude1 = latitude1;
    }

    public void setLatitude2(double latitude2) {
        this.latitude2 = latitude2;
    }

    public void setLongitude1(double longitude1) {
        this.longitude1 = longitude1;
    }

    public void setLongitude2(double longitude2) {
        this.longitude2 = longitude2;
    }

    public void setName(String name) {
        this.name = name;
    }
}
