package edu.espe.inventory.service;

import edu.espe.inventory.dto.DeviceRequest;
import edu.espe.inventory.dto.DeviceResponse;
import edu.espe.inventory.dto.InventoryStats;
import edu.espe.inventory.exception.DuplicateSerialException;
import edu.espe.inventory.exception.NegativeStockException;
import edu.espe.inventory.model.Device;
import edu.espe.inventory.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }
    
    public DeviceResponse create(DeviceRequest request) {
        // Validar serial duplicado
        if (deviceRepository.existsBySerial(request.getSerial())) {
            throw new DuplicateSerialException(request.getSerial());
        }
        
        // Validar stock negativo
        if (request.getStock() != null && request.getStock() < 0) {
            throw new NegativeStockException("El stock no puede ser negativo");
        }
        
        Device device = new Device();
        device.setName(request.getName());
        device.setSerial(request.getSerial());
        device.setCategory(request.getCategory());
        device.setStock(request.getStock() != null ? request.getStock() : 0);
        device.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);
        device.setDeleted(false);
        
        Device savedDevice = deviceRepository.save(device);
        return convertToResponse(savedDevice);
    }
    
    public List<DeviceResponse> findAll() {
        return deviceRepository.findByDeletedFalse().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public DeviceResponse findById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        
        if (device.getDeleted()) {
            throw new RuntimeException("Dispositivo no encontrado");
        }
        
        return convertToResponse(device);
    }
    
    public DeviceResponse updateStock(Long id, Integer stock) {
        if (stock < 0) {
            throw new NegativeStockException("El stock no puede ser negativo");
        }
        
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        
        if (device.getDeleted()) {
            throw new RuntimeException("Dispositivo no encontrado");
        }
        
        device.setStock(stock);
        Device updatedDevice = deviceRepository.save(device);
        return convertToResponse(updatedDevice);
    }
    
    public DeviceResponse deactivate(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        
        if (device.getDeleted()) {
            throw new RuntimeException("Dispositivo no encontrado");
        }
        
        device.setAvailable(false);
        Device updatedDevice = deviceRepository.save(device);
        return convertToResponse(updatedDevice);
    }
    
    public void delete(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        
        if (device.getDeleted()) {
            throw new RuntimeException("Dispositivo no encontrado");
        }
        
        device.setDeleted(true);
        deviceRepository.save(device);
    }
    
    public List<DeviceResponse> findByCategory(String category) {
        return deviceRepository.findByDeletedFalseAndCategoryContainingIgnoreCase(category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DeviceResponse> findLowStock() {
        return deviceRepository.findByDeletedFalseAndStockLessThan(5).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public InventoryStats getStats() {
        long total = deviceRepository.countByDeletedFalse();
        long available = deviceRepository.countByDeletedFalseAndAvailableTrue();
        long unavailable = deviceRepository.countByDeletedFalseAndAvailableFalse();
        
        return new InventoryStats(total, available, unavailable);
    }
    
    private DeviceResponse convertToResponse(Device device) {
        DeviceResponse response = new DeviceResponse();
        response.setId(device.getId());
        response.setName(device.getName());
        response.setSerial(device.getSerial());
        response.setCategory(device.getCategory());
        response.setStock(device.getStock());
        response.setAvailable(device.getAvailable());
        response.setCreatedAt(device.getCreatedAt());
        response.setUpdatedAt(device.getUpdatedAt());
        return response;
    }
}
