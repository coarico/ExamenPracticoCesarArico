package edu.espe.inventory.service;

import edu.espe.inventory.dto.DeviceRequest;
import edu.espe.inventory.exception.DuplicateSerialException;
import edu.espe.inventory.exception.NegativeStockException;
import edu.espe.inventory.model.Device;
import edu.espe.inventory.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private DeviceRequest deviceRequest;
    private Device device;

    @BeforeEach
    void setUp() {
        deviceRequest = new DeviceRequest();
        deviceRequest.setName("Test Device");
        deviceRequest.setSerial("ABC-001");
        deviceRequest.setCategory("Laptop");
        deviceRequest.setStock(10);
        deviceRequest.setAvailable(true);

        device = new Device();
        device.setId(1L);
        device.setName("Test Device");
        device.setSerial("ABC-001");
        device.setCategory("Laptop");
        device.setStock(10);
        device.setAvailable(true);
        device.setDeleted(false);
    }

    // Prueba 1: Evitar serial duplicado
    @Test
    void shouldThrowExceptionWhenSerialIsDuplicated() {
        // Arrange
        when(deviceRepository.existsBySerial("ABC-001")).thenReturn(true);

        // Act & Assert
        DuplicateSerialException exception = assertThrows(
                DuplicateSerialException.class,
                () -> deviceService.create(deviceRequest)
        );
        assertEquals("Dispositivo con serial ABC-001 ya existe", exception.getMessage());
        verify(deviceRepository, never()).save(any(Device.class));
    }

    // Prueba 2: No permitir stock negativo
    @Test
    void shouldThrowExceptionWhenStockIsNegative() {
        // Arrange
        deviceRequest.setStock(-5);

        // Act & Assert
        NegativeStockException exception = assertThrows(
                NegativeStockException.class,
                () -> deviceService.create(deviceRequest)
        );
        assertEquals("El stock no puede ser negativo", exception.getMessage());
        verify(deviceRepository, never()).save(any(Device.class));
    }

    // Prueba 3: Desactivar dispositivo
    @Test
    void shouldDeactivateDeviceSuccessfully() {
        // Arrange
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = deviceService.deactivate(1L);

        // Assert
        assertFalse(result.getAvailable());
        assertEquals("Test Device", result.getName());
        assertEquals("ABC-001", result.getSerial());
        assertEquals("Laptop", result.getCategory());
        verify(deviceRepository).save(device);
    }

    // Prueba 4: Estadísticas de inventario
    @Test
    void shouldReturnCorrectInventoryStats() {
        // Arrange
        when(deviceRepository.countByDeletedFalse()).thenReturn(3L);
        when(deviceRepository.countByDeletedFalseAndAvailableTrue()).thenReturn(2L);
        when(deviceRepository.countByDeletedFalseAndAvailableFalse()).thenReturn(1L);

        // Act
        var stats = deviceService.getStats();

        // Assert
        assertEquals(3L, stats.getTotal());
        assertEquals(2L, stats.getAvailable());
        assertEquals(1L, stats.getUnavailable());
    }

    // Prueba 5: Eliminación lógica
    @Test
    void shouldPerformLogicalDeletion() {
        // Arrange
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        deviceService.delete(1L);

        // Assert
        assertTrue(device.getDeleted());
        verify(deviceRepository).save(device);
    }

    @Test
    void shouldNotFindDeletedDevice() {
        // Arrange
        device.setDeleted(true);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deviceService.findById(1L)
        );
        assertEquals("Dispositivo no encontrado", exception.getMessage());
    }

    // Prueba 6: Búsqueda parcial por categoría
    @Test
    void shouldFindDevicesByPartialCategory() {
        // Arrange
        Device laptop = new Device();
        laptop.setName("Laptop");
        laptop.setCategory("Laptop");
        laptop.setDeleted(false);

        Device laptopGamer = new Device();
        laptopGamer.setName("Laptop Gamer");
        laptopGamer.setCategory("Laptop Gamer");
        laptopGamer.setDeleted(false);

        Device router = new Device();
        router.setName("Router");
        router.setCategory("Router");
        router.setDeleted(false);

        when(deviceRepository.findByDeletedFalseAndCategoryContainingIgnoreCase("lap"))
                .thenReturn(Arrays.asList(laptop, laptopGamer));

        // Act
        var results = deviceService.findByCategory("lap");

        // Assert
        assertEquals(2, results.size());
        assertEquals("Laptop", results.get(0).getName());
        assertEquals("Laptop Gamer", results.get(1).getName());
        verify(deviceRepository).findByDeletedFalseAndCategoryContainingIgnoreCase("lap");
    }

    // Bonus: Low stock endpoint
    @Test
    void shouldFindLowStockDevices() {
        // Arrange
        Device lowStockDevice = new Device();
        lowStockDevice.setName("Low Stock Device");
        lowStockDevice.setStock(3);
        lowStockDevice.setDeleted(false);

        when(deviceRepository.findByDeletedFalseAndStockLessThan(5))
                .thenReturn(Arrays.asList(lowStockDevice));

        // Act
        var results = deviceService.findLowStock();

        // Assert
        assertEquals(1, results.size());
        assertEquals("Low Stock Device", results.get(0).getName());
        assertEquals(3, results.get(0).getStock());
        verify(deviceRepository).findByDeletedFalseAndStockLessThan(5);
    }
}
