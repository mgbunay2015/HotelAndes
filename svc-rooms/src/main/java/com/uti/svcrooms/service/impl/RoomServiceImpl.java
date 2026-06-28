package com.uti.svcrooms.service.impl;

import com.uti.svcrooms.dto.AvailabilityResponse;
import com.uti.svcrooms.dto.AvailabilityUpdateRequest;
import com.uti.svcrooms.dto.RoomRequest;
import com.uti.svcrooms.dto.RoomResponse;
import com.uti.svcrooms.exception.BusinessRuleException;
import com.uti.svcrooms.exception.DuplicateResourceException;
import com.uti.svcrooms.exception.ResourceNotFoundException;
import com.uti.svcrooms.mapper.RoomMapper;
import com.uti.svcrooms.model.Room;
import com.uti.svcrooms.repository.RoomRepository;
import com.uti.svcrooms.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de habitaciones con reglas de negocio del hotel.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    /**
     * Lista todas las habitaciones registradas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> findAll() {
        log.info("Listing all rooms");
        return roomRepository.findAll().stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    /**
     * Obtiene una habitación por su identificador.
     */
    @Override
    @Transactional(readOnly = true)
    public RoomResponse findById(Long id) {
        log.info("Finding room by id={}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return roomMapper.toResponse(room);
    }

    /**
     * Registra una nueva habitación validando unicidad y capacidad.
     */
    @Override
    @Transactional
    public RoomResponse create(RoomRequest request) {
        log.info("Creating room with number={}", request.getRoomNumber());

        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException("Room number already exists: " + request.getRoomNumber());
        }

        validateAvailabilityRules(request.getAvailableRooms(), request.getTotalCapacity());

        Room room = roomMapper.toEntity(request);
        Room saved = roomRepository.save(room);
        log.info("Room created successfully with id={}", saved.getId());
        return roomMapper.toResponse(saved);
    }

    /**
     * Consulta la disponibilidad actual de una habitación.
     */
    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailability(Long id) {
        log.info("Checking availability for room id={}", id);
        Room room = findRoomOrThrow(id);
        return roomMapper.toAvailabilityResponse(room);
    }

    /**
     * Actualiza el contador de habitaciones disponibles.
     */
    @Override
    @Transactional
    public AvailabilityResponse updateAvailability(Long id, AvailabilityUpdateRequest request) {
        log.info("Updating availability for room id={} to {}", id, request.getAvailableRooms());
        Room room = findRoomOrThrow(id);
        validateAvailabilityRules(request.getAvailableRooms(), room.getTotalCapacity());
        room.setAvailableRooms(request.getAvailableRooms());
        Room updated = roomRepository.save(room);
        return roomMapper.toAvailabilityResponse(updated);
    }

    private Room findRoomOrThrow(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    private void validateAvailabilityRules(Integer availableRooms, Integer totalCapacity) {
        if (availableRooms < 0) {
            throw new BusinessRuleException("Available rooms cannot be negative");
        }
        if (availableRooms > totalCapacity) {
            throw new BusinessRuleException("Available rooms cannot exceed total capacity");
        }
    }
}
