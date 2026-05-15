package edu.espe.inventory.web.controller;

import edu.espe.inventory.dto.DeviceRequest;
import edu.espe.inventory.dto.DeviceResponse;
import edu.espe.inventory.dto.InventoryStats;
import edu.espe.inventory.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cesararico/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceRequest request) {
        DeviceResponse response = deviceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> findAll() {
        List<DeviceResponse> devices = deviceService.findAll();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> findById(@PathVariable Long id) {
        DeviceResponse device = deviceService.findById(id);
        return ResponseEntity.ok(device);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<DeviceResponse> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        DeviceResponse device = deviceService.updateStock(id, stock);
        return ResponseEntity.ok(device);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<DeviceResponse> deactivate(@PathVariable Long id) {
        DeviceResponse device = deviceService.deactivate(id);
        return ResponseEntity.ok(device);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<DeviceResponse>> findByCategory(@RequestParam String category) {
        List<DeviceResponse> devices = deviceService.findByCategory(category);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<DeviceResponse>> findLowStock() {
        List<DeviceResponse> devices = deviceService.findLowStock();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/stats")
    public ResponseEntity<InventoryStats> getStats() {
        InventoryStats stats = deviceService.getStats();
        return ResponseEntity.ok(stats);
    }
}
