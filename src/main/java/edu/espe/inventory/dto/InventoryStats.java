package edu.espe.inventory.dto;

public class InventoryStats {
    
    private Long total;
    private Long available;
    private Long unavailable;
    
    public InventoryStats(Long total, Long available, Long unavailable) {
        this.total = total;
        this.available = available;
        this.unavailable = unavailable;
    }
    
    // Getters and Setters
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public Long getAvailable() {
        return available;
    }
    
    public void setAvailable(Long available) {
        this.available = available;
    }
    
    public Long getUnavailable() {
        return unavailable;
    }
    
    public void setUnavailable(Long unavailable) {
        this.unavailable = unavailable;
    }
}
