package edu.espe.inventory.repository;

import edu.espe.inventory.model.Device;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    void shouldSaveAndFindDeviceBySerial() {
        // Arrange
        Device device = new Device();
        device.setName("Test Device");
        device.setSerial("ABC-001");
        device.setCategory("Laptop");
        device.setStock(10);
        device.setAvailable(true);
        device.setDeleted(false);

        // Act
        deviceRepository.save(device);
        var result = deviceRepository.existsBySerial("ABC-001");

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldFindActiveDevices() {
        // Arrange
        Device activeDevice = new Device();
        activeDevice.setName("Active Device");
        activeDevice.setSerial("ABC-001");
        activeDevice.setCategory("Laptop");
        activeDevice.setStock(10);
        activeDevice.setAvailable(true);
        activeDevice.setDeleted(false);

        Device deletedDevice = new Device();
        deletedDevice.setName("Deleted Device");
        deletedDevice.setSerial("ABC-002");
        deletedDevice.setCategory("Router");
        deletedDevice.setStock(5);
        deletedDevice.setAvailable(true);
        deletedDevice.setDeleted(true);

        // Act
        deviceRepository.save(activeDevice);
        deviceRepository.save(deletedDevice);
        List<Device> activeDevices = deviceRepository.findByDeletedFalse();

        // Assert
        assertEquals(1, activeDevices.size());
        assertEquals("Active Device", activeDevices.get(0).getName());
    }

    @Test
    void shouldFindDevicesByPartialCategory() {
        // Arrange
        Device laptop = new Device();
        laptop.setName("Laptop");
        laptop.setSerial("ABC-001");
        laptop.setCategory("Laptop");
        laptop.setStock(10);
        laptop.setAvailable(true);
        laptop.setDeleted(false);

        Device laptopGamer = new Device();
        laptopGamer.setName("Laptop Gamer");
        laptopGamer.setSerial("ABC-002");
        laptopGamer.setCategory("Laptop Gamer");
        laptopGamer.setStock(5);
        laptopGamer.setAvailable(true);
        laptopGamer.setDeleted(false);

        Device router = new Device();
        router.setName("Router");
        router.setSerial("ABC-003");
        router.setCategory("Router");
        router.setStock(3);
        router.setAvailable(true);
        router.setDeleted(false);

        // Act
        deviceRepository.save(laptop);
        deviceRepository.save(laptopGamer);
        deviceRepository.save(router);
        List<Device> laptops = deviceRepository.findByDeletedFalseAndCategoryContainingIgnoreCase("lap");

        // Assert
        assertEquals(2, laptops.size());
        assertTrue(laptops.stream().anyMatch(d -> d.getName().equals("Laptop")));
        assertTrue(laptops.stream().anyMatch(d -> d.getName().equals("Laptop Gamer")));
        assertFalse(laptops.stream().anyMatch(d -> d.getName().equals("Router")));
    }
}
