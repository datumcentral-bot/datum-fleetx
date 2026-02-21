package com.datum.fleetx.service;

import com.datum.fleetx.dto.truck.TruckRequest;
import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.Truck;
import com.datum.fleetx.exception.ResourceNotFoundException;
import com.datum.fleetx.repository.CompanyRepository;
import com.datum.fleetx.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TruckService {

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public List<Truck> getAllTrucksByCompany(UUID companyId) {
        return truckRepository.findByCompanyId(companyId);
    }

    public Truck getTruckById(UUID id, UUID companyId) {
        return truckRepository.findById(id)
            .filter(truck -> truck.getCompany().getId().equals(companyId))
            .orElseThrow(() -> new ResourceNotFoundException("Truck", "id", id));
    }

    public Truck createTruck(TruckRequest request, UUID companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        // Check for duplicate truck number
        if (truckRepository.existsByTruckNumberAndCompanyId(request.getTruckNumber(), companyId)) {
            throw new IllegalArgumentException("Truck number already exists: " + request.getTruckNumber());
        }

        Truck truck = new Truck();
        truck.setTruckNumber(request.getTruckNumber());
        truck.setVin(request.getVin());
        truck.setMake(request.getMake());
        truck.setModel(request.getModel());
        truck.setYear(request.getYear());
        truck.setPlateNumber(request.getPlateNumber());
        truck.setTruckType(request.getTruckType());
        truck.setStatus(Truck.TruckStatus.AVAILABLE);
        truck.setCapacityWeight(request.getCapacityWeight());
        truck.setCapacityVolume(request.getCapacityVolume());
        truck.setFuelType(request.getFuelType());
        truck.setFuelEfficiency(request.getFuelEfficiency());
        truck.setCurrentMileage(request.getCurrentMileage());
        truck.setNotes(request.getNotes());
        truck.setCompany(company);
        
        return truckRepository.save(truck);
    }

    public Truck updateTruck(UUID id, TruckRequest request, UUID companyId) {
        Truck truck = getTruckById(id, companyId);
        
        if (request.getTruckNumber() != null) truck.setTruckNumber(request.getTruckNumber());
        if (request.getVin() != null) truck.setVin(request.getVin());
        if (request.getMake() != null) truck.setMake(request.getMake());
        if (request.getModel() != null) truck.setModel(request.getModel());
        if (request.getYear() != null) truck.setYear(request.getYear());
        if (request.getPlateNumber() != null) truck.setPlateNumber(request.getPlateNumber());
        if (request.getTruckType() != null) truck.setTruckType(request.getTruckType());
        if (request.getCapacityWeight() != null) truck.setCapacityWeight(request.getCapacityWeight());
        if (request.getCapacityVolume() != null) truck.setCapacityVolume(request.getCapacityVolume());
        if (request.getFuelType() != null) truck.setFuelType(request.getFuelType());
        if (request.getFuelEfficiency() != null) truck.setFuelEfficiency(request.getFuelEfficiency());
        if (request.getCurrentMileage() != null) truck.setCurrentMileage(request.getCurrentMileage());
        if (request.getNotes() != null) truck.setNotes(request.getNotes());
        
        return truckRepository.save(truck);
    }

    public void deleteTruck(UUID id, UUID companyId) {
        Truck truck = getTruckById(id, companyId);
        truck.setActive(false);
        truckRepository.save(truck);
    }

    public Truck updateTruckStatus(UUID id, Truck.TruckStatus status, UUID companyId) {
        Truck truck = getTruckById(id, companyId);
        truck.setStatus(status);
        return truckRepository.save(truck);
    }

    public Truck updateTruckLocation(UUID id, Double latitude, Double longitude, UUID companyId) {
        Truck truck = getTruckById(id, companyId);
        truck.setCurrentLatitude(latitude);
        truck.setCurrentLongitude(longitude);
        truck.setLastLocationUpdate(java.time.Instant.now());
        return truckRepository.save(truck);
    }

    public List<Truck> getAvailableTrucks(UUID companyId) {
        return truckRepository.findAvailableByCompanyId(companyId);
    }

    public long getTotalTrucksCount(UUID companyId) {
        return truckRepository.countActiveByCompanyId(companyId);
    }

    public long getAvailableTrucksCount(UUID companyId) {
        return truckRepository.findAvailableByCompanyId(companyId).size();
    }

    public long getInTransitTrucksCount(UUID companyId) {
        return truckRepository.findByCompanyIdAndStatus(companyId, Truck.TruckStatus.IN_TRANSIT).size();
    }
}
