package com.easylabour.trackingservice.domain.repository;

import com.easylabour.trackingservice.domain.model.LocationHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LocationRepository extends MongoRepository<LocationHistory, String> {
    List<LocationHistory> findByBookingId(String bookingId);
}
