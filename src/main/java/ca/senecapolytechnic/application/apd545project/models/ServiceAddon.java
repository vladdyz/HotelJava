package ca.senecapolytechnic.application.apd545project.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="ServiceAddons", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class ServiceAddon implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length=100, unique = true)
    private String name;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private PricingModel pricingModel;

    public ServiceAddon() {
    }

    public ServiceAddon(String name, double price, PricingModel pricingModel) {
        this.name = name;
        this.price = price;
        this.pricingModel = pricingModel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PricingModel getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(PricingModel pricingModel) {
        this.pricingModel = pricingModel;
    }

    @Override
    public String toString() {
        return "ServiceAddon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", pricingModel=" + pricingModel +
                '}';
    }
}
