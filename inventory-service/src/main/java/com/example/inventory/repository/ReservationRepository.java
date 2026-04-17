package com.example.inventory.repository;

import com.example.inventory.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    
    @Query("SELECT r FROM Reservation r WHERE r.expiresAt < :now")
    List<Reservation> findExpiredReservations(@Param("now") Instant now);
    
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.reservationId = :reservationId")
    void deleteByReservationId(@Param("reservationId") String reservationId);
}