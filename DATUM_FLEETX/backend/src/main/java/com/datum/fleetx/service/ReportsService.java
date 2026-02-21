package com.datum.fleetx.service;

import com.datum.fleetx.entity.Company;
import com.datum.fleetx.entity.Driver;
import com.datum.fleetx.entity.Load;
import com.datum.fleetx.entity.Truck;
import com.datum.fleetx.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reports Service - Advanced analytics and reporting for fleet operations
 */
@Service
@RequiredArgsConstructor
public class ReportsService {

    private final LoadRepository loadRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;
    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;

    /**
     * Get revenue by truck report
     */
    public Map<String, Object> getRevenueByTruck(UUID companyId, ZonedDateTime startDate, ZonedDateTime endDate) {
        List<Load> allLoads = loadRepository.findByCompanyId(companyId);
        List<Load> loads = allLoads.stream()
            .filter(l -> l.getPickupDateTime() != null)
            .filter(l -> !l.getPickupDateTime().isBefore(startDate) && !l.getPickupDateTime().isAfter(endDate))
            .collect(Collectors.toList());

        Map<UUID, BigDecimal> revenueByTruck = new HashMap<>();
        Map<UUID, Integer> loadsByTruck = new HashMap<>();

        for (Load load : loads) {
            if (load.getTruck() != null && load.getRate() != null) {
                UUID truckId = load.getTruck().getId();
                revenueByTruck.merge(truckId, load.getRate(), BigDecimal::add);
                loadsByTruck.merge(truckId, 1, Integer::sum);
            }
        }

        List<Map<String, Object>> truckData = new ArrayList<>();
        for (UUID truckId : revenueByTruck.keySet()) {
            Truck truck = truckRepository.findById(truckId).orElse(null);
            if (truck != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("truckId", truckId);
                data.put("truckNumber", truck.getTruckNumber());
                data.put("revenue", revenueByTruck.get(truckId));
                data.put("totalLoads", loadsByTruck.get(truckId));
                BigDecimal avgRevenue = revenueByTruck.get(truckId)
                    .divide(BigDecimal.valueOf(loadsByTruck.get(truckId)), 2, RoundingMode.HALF_UP);
                data.put("avgRevenuePerLoad", avgRevenue);
                truckData.add(data);
            }
        }

        truckData.sort((a, b) -> 
            ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));

        Map<String, Object> result = new HashMap<>();
        result.put("trucks", truckData);
        result.put("totalRevenue", revenueByTruck.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        result.put("totalLoads", loads.size());

        return result;
    }

    /**
     * Get revenue by driver report
     */
    public Map<String, Object> getRevenueByDriver(UUID companyId, ZonedDateTime startDate, ZonedDateTime endDate) {
        List<Load> allLoads = loadRepository.findByCompanyId(companyId);
        List<Load> loads = allLoads.stream()
            .filter(l -> l.getPickupDateTime() != null)
            .filter(l -> !l.getPickupDateTime().isBefore(startDate) && !l.getPickupDateTime().isAfter(endDate))
            .collect(Collectors.toList());

        Map<UUID, BigDecimal> revenueByDriver = new HashMap<>();
        Map<UUID, Integer> loadsByDriver = new HashMap<>();

        for (Load load : loads) {
            if (load.getDriver() != null && load.getRate() != null) {
                UUID driverId = load.getDriver().getId();
                revenueByDriver.merge(driverId, load.getRate(), BigDecimal::add);
                loadsByDriver.merge(driverId, 1, Integer::sum);
            }
        }

        List<Map<String, Object>> driverData = new ArrayList<>();
        for (UUID driverId : revenueByDriver.keySet()) {
            Driver driver = driverRepository.findById(driverId).orElse(null);
            if (driver != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("driverId", driverId);
                data.put("driverName", driver.getFirstName() + " " + driver.getLastName());
                data.put("revenue", revenueByDriver.get(driverId));
                data.put("totalLoads", loadsByDriver.get(driverId));
                driverData.add(data);
            }
        }

        driverData.sort((a, b) -> 
            ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));

        Map<String, Object> result = new HashMap<>();
        result.put("drivers", driverData);
        result.put("totalRevenue", revenueByDriver.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        return result;
    }

    /**
     * Get customer profitability report
     */
    public Map<String, Object> getCustomerProfitability(UUID companyId, ZonedDateTime startDate, ZonedDateTime endDate) {
        List<Load> allLoads = loadRepository.findByCompanyId(companyId);
        List<Load> loads = allLoads.stream()
            .filter(l -> l.getPickupDateTime() != null)
            .filter(l -> !l.getPickupDateTime().isBefore(startDate) && !l.getPickupDateTime().isAfter(endDate))
            .collect(Collectors.toList());

        Map<UUID, BigDecimal> revenueByCustomer = new HashMap<>();
        Map<UUID, Integer> loadsByCustomer = new HashMap<>();

        for (Load load : loads) {
            if (load.getCustomer() != null && load.getRate() != null) {
                UUID customerId = load.getCustomer().getId();
                revenueByCustomer.merge(customerId, load.getRate(), BigDecimal::add);
                loadsByCustomer.merge(customerId, 1, Integer::sum);
            }
        }

        List<Map<String, Object>> customerData = new ArrayList<>();
        for (UUID customerId : revenueByCustomer.keySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("customerId", customerId);
            data.put("revenue", revenueByCustomer.get(customerId));
            data.put("totalLoads", loadsByCustomer.get(customerId));
            customerData.add(data);
        }

        customerData.sort((a, b) -> 
            ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));

        Map<String, Object> result = new HashMap<>();
        result.put("customers", customerData);
        result.put("totalRevenue", revenueByCustomer.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add));

        return result;
    }

    /**
     * Get on-time delivery percentage
     */
    public Map<String, Object> getOnTimeDeliveryStats(UUID companyId, ZonedDateTime startDate, ZonedDateTime endDate) {
        List<Load> allLoads = loadRepository.findByCompanyId(companyId);
        List<Load> loads = allLoads.stream()
            .filter(l -> l.getPickupDateTime() != null)
            .filter(l -> !l.getPickupDateTime().isBefore(startDate) && !l.getPickupDateTime().isAfter(endDate))
            .collect(Collectors.toList());

        long totalDelivered = loads.stream()
            .filter(l -> l.getStatus() == Load.LoadStatus.DELIVERED)
            .count();

        long onTime = loads.stream()
            .filter(l -> l.getStatus() == Load.LoadStatus.DELIVERED)
            .filter(l -> l.getDeliveryDateTime() != null && l.getEstimatedArrival() != null)
            .filter(l -> !l.getDeliveryDateTime().isAfter(l.getEstimatedArrival()))
            .count();

        Map<String, Object> result = new HashMap<>();
        result.put("totalDelivered", totalDelivered);
        result.put("onTime", onTime);
        result.put("late", totalDelivered - onTime);
        result.put("onTimePercentage", totalDelivered > 0 
            ? BigDecimal.valueOf(onTime * 100.0 / totalDelivered).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO);

        return result;
    }

    /**
     * Get fleet utilization report
     */
    public Map<String, Object> getFleetUtilization(UUID companyId, ZonedDateTime startDate, ZonedDateTime endDate) {
        List<Truck> trucks = truckRepository.findByCompanyId(companyId);
        
        List<Load> allLoads = loadRepository.findByCompanyId(companyId);
        List<Load> loads = allLoads.stream()
            .filter(l -> l.getPickupDateTime() != null)
            .filter(l -> !l.getPickupDateTime().isBefore(startDate) && !l.getPickupDateTime().isAfter(endDate))
            .collect(Collectors.toList());

        long daysInPeriod = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long totalTruckDays = trucks.size() * daysInPeriod;

        long assignedTruckDays = loads.stream()
            .filter(l -> l.getTruck() != null)
            .map(l -> l.getTruck().getId())
            .distinct()
            .count() * daysInPeriod;

        Map<String, Object> result = new HashMap<>();
        result.put("totalTrucks", trucks.size());
        result.put("activeTrucks", trucks.stream().filter(Truck::getActive).count());
        result.put("totalTruckDays", totalTruckDays);
        result.put("assignedTruckDays", assignedTruckDays);
        result.put("utilizationPercentage", totalTruckDays > 0
            ? BigDecimal.valueOf(assignedTruckDays * 100.0 / totalTruckDays)
                .setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO);

        return result;
    }

    /**
     * Get monthly revenue trend
     */
    public List<Map<String, Object>> getMonthlyRevenueTrend(UUID companyId, int year) {
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        ZonedDateTime startOfYear = ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, ZonedDateTime.now().getZone());

        for (int month = 1; month <= 12; month++) {
            ZonedDateTime startDate = startOfYear.withMonth(month);
            ZonedDateTime endDate = startDate.plusMonths(1);

            List<Load> allLoads = loadRepository.findByCompanyId(companyId);
            List<Load> loads = allLoads.stream()
                .filter(l -> l.getPickupDateTime() != null)
                .filter(l -> !l.getPickupDateTime().isBefore(startDate) && l.getPickupDateTime().isBefore(endDate))
                .collect(Collectors.toList());

            BigDecimal totalRevenue = loads.stream()
                .filter(l -> l.getRate() != null)
                .map(Load::getRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> data = new HashMap<>();
            data.put("month", month);
            data.put("monthName", startDate.getMonth().name());
            data.put("revenue", totalRevenue);
            data.put("loadsCount", loads.size());
            monthlyData.add(data);
        }

        return monthlyData;
    }

    /**
     * Get executive summary dashboard
     */
    public Map<String, Object> getExecutiveSummary(UUID companyId) {
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            return Collections.emptyMap();
        }

        ZonedDateTime today = ZonedDateTime.now();
        ZonedDateTime startOfMonth = today.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime startOfYear = today.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);

        // Monthly stats
        List<Load> allLoads = loadRepository.findByCompanyId(companyId);
        List<Load> monthlyLoads = allLoads.stream()
            .filter(l -> l.getPickupDateTime() != null)
            .filter(l -> !l.getPickupDateTime().isBefore(startOfMonth))
            .collect(Collectors.toList());

        BigDecimal monthlyRevenue = monthlyLoads.stream()
            .filter(l -> l.getRate() != null)
            .map(Load::getRate)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Yearly stats
        List<Load> yearlyLoads = allLoads.stream()
            .filter(l -> l.getPickupDateTime() != null)
            .filter(l -> !l.getPickupDateTime().isBefore(startOfYear))
            .collect(Collectors.toList());

        BigDecimal yearlyRevenue = yearlyLoads.stream()
            .filter(l -> l.getRate() != null)
            .map(Load::getRate)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Fleet stats
        List<Truck> trucks = truckRepository.findByCompanyId(companyId);
        long activeTrucks = trucks.stream()
            .filter(t -> t.getStatus() == Truck.TruckStatus.AVAILABLE)
            .count();

        // Driver stats
        List<Driver> drivers = driverRepository.findByCompanyId(companyId);
        long activeDrivers = drivers.stream()
            .filter(d -> d.getStatus() == Driver.DriverStatus.AVAILABLE)
            .count();

        // Load stats
        long activeLoads = allLoads.stream()
            .filter(l -> l.getStatus() == Load.LoadStatus.IN_TRANSIT)
            .count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("companyName", company.getCompanyName());
        summary.put("monthlyRevenue", monthlyRevenue);
        summary.put("monthlyLoads", monthlyLoads.size());
        summary.put("yearlyRevenue", yearlyRevenue);
        summary.put("yearlyLoads", yearlyLoads.size());
        summary.put("totalTrucks", trucks.size());
        summary.put("activeTrucks", activeTrucks);
        summary.put("totalDrivers", drivers.size());
        summary.put("activeDrivers", activeDrivers);
        summary.put("activeLoads", activeLoads);

        return summary;
    }
}
