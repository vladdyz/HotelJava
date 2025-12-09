package ca.senecapolytechnic.application.apd545project.config;

import ca.senecapolytechnic.application.apd545project.models.Role;

public class DiscountPolicy {

    // admin max is 15, manager max is 30
    public int maxDiscountForRole(Role role) {
        if (role == Role.MANAGER) return 30;
        return 15; // only admins can apply discount policy (default)
    }

    // calculate the billing total using the discount applied
    public double applyDiscount(double totalAmount, int discountPercent) {
        if (discountPercent <= 0) return totalAmount;
        return totalAmount * (1.0 - (discountPercent / 100.0));
    }
}