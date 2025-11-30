package ca.senecapolytechnic.application.apd545project.config;

public class LoyaltyPolicy {
    // determines constants for loyalty point accruals and redemptions
    private double earningRate = 1.0;
    private int redemptionCap = 1000;

    // Singleton pattern ensures one global policy
    private static final LoyaltyPolicy instance = new LoyaltyPolicy();
    public static LoyaltyPolicy getInstance() {
        return instance;
    }

    private LoyaltyPolicy() {}

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
}

