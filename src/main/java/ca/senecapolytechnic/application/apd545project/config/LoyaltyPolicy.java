package ca.senecapolytechnic.application.apd545project.config;

public class LoyaltyPolicy {
    // determines constants for loyalty point accruals and redemptions
    private double earningRate = 1.0;
    private int redemptionCap = 1000;

    // make sure policy is global and applies to everyone (singleton)
    private static final LoyaltyPolicy instance = new LoyaltyPolicy();
    public static LoyaltyPolicy getInstance() {
        return instance;
    }

    public LoyaltyPolicy() {}

    public double getEarningRate() {
        return earningRate;
    }

    public void setEarningRate(double earningRate) {
        this.earningRate = earningRate;
    }

    public int getRedemptionCap() {
        return redemptionCap;
    }

    public void setRedemptionCap(int redemptionCap) {
        this.redemptionCap = redemptionCap;
    }

    public double convertPointsToDollars(int points) {
        return points / 100.0;  // 100 points = $1
    }
}

