package edu.espe.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class DeviceRequest {
    
    @NotBlank(message = "El nombre del dispositivo es requerido")
    private String name;
    
    @NotBlank(message = "El número de serial es requerido")
    private String serial;
    
    @NotBlank(message = "La categoría es requerida")
    private String category;
    
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    private Boolean available = true;
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSerial() {
        return serial;
    }
    
    public void setSerial(String serial) {
        this.serial = serial;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public Boolean getAvailable() {
        return available;
    }
    
    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
