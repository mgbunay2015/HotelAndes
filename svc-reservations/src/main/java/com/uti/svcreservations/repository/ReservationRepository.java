package com.uti.svcreservations.repository;

import com.uti.svcreservations.model.Reservation;
import com.uti.svcreservations.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Reservation.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByGuestEmail(String guestEmail);

    boolean existsByGuestEmailAndRoomIdAndStatus(String guestEmail, Long roomId, ReservationStatus status);
}
