package ca.senecapolytechnic.application.apd545project.config;

public class PricingPolicy {
    private double taxRate = 0.13;
    private double wifiPricePerNight = 10.0;
    private double breakfastPerPersonPerNight = 5.0;
    private double parkingPerNight = 20.0;
    private double spaFlat = 40.0;

    public PricingPolicy() {
        taxRate = 0.13;
        wifiPricePerNight = 10.0;
        breakfastPerPersonPerNight = 5.0;
        parkingPerNight = 20.0;
        spaFlat = 40.0;
    }

    public double getWifiPricePerNight() {
        return wifiPricePerNight;
    }

    public void setWifiPricePerNight(double wifiPricePerNight) {
        this.wifiPricePerNight = wifiPricePerNight;
    }

    public double getBreakfastPerPersonPerNight() {
        return breakfastPerPersonPerNight;
    }

    public void setBreakfastPerPersonPerNight(double breakfastPerPersonPerNight) {
        this.breakfastPerPersonPerNight = breakfastPerPersonPerNight;
    }

    public double getParkingPerNight() {
        return parkingPerNight;
    }

    public void setParkingPerNight(double parkingPerNight) {
        this.parkingPerNight = parkingPerNight;
    }

    public double getSpaFlat() {
        return spaFlat;
    }

    public void setSpaFlat(double spaFlat) {
        this.spaFlat = spaFlat;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }
}
