package org.example.models.responses;

public class CouriersIdOrderCount {
    private Long id;
    private String ordersCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(String ordersCount) {
        this.ordersCount = ordersCount;
    }
}
