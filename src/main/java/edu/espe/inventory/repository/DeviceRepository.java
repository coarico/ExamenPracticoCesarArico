package edu.espe.inventory.repository;

import edu.espe.inventory.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    boolean existsBySerial(String serial);
    
    List<Device> findByDeletedFalse();
    
    List<Device> findByDeletedFalseAndAvailableTrue();
    
    List<Device> findByDeletedFalseAndAvailableFalse();
    
    List<Device> findByDeletedFalseAndCategoryContainingIgnoreCase(String category);
    
    List<Device> findByDeletedFalseAndStockLessThan(Integer stock);
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.deleted = false")
    long countByDeletedFalse();
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.deleted = false AND d.available = true")
    long countByDeletedFalseAndAvailableTrue();
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.deleted = false AND d.available = false")
    long countByDeletedFalseAndAvailableFalse();
}
