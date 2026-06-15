package com.bookdemo11.room.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookdemo11.booking.repository.RoomOrderRepository;
import com.bookdemo11.room.entity.RoomType;
import com.bookdemo11.room.repository.RoomRepository;
import com.bookdemo11.room.repository.RoomTypeRepository;

@Service
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final RoomOrderRepository roomOrderRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository,
                           RoomRepository roomRepository,
                           RoomOrderRepository roomOrderRepository) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.roomOrderRepository = roomOrderRepository;
    }

    public List<RoomType> getAvailableRoomTypes() {
        return roomTypeRepository.findByRoomSaleStatus((byte) 1);
    }

    public Optional<RoomType> findById(Integer id) {
        return roomTypeRepository.findById(id);
    }

    public List<RoomType> findAll() {
        return roomTypeRepository.findAll();
    }

    public int getTotalRooms(Integer roomTypeId) {
        return (int) roomRepository.countByRoomTypeRoomTypeIdAndRoomStatus(roomTypeId, 1);
    }

    public int getAvailableCount(Integer roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        int total = getTotalRooms(roomTypeId);
        int booked = roomOrderRepository.countBookedRooms(roomTypeId, checkIn, checkOut);
        return Math.max(0, total - booked);
    }

    public Map<Integer, Integer> getAvailabilityMap(LocalDate checkIn, LocalDate checkOut) {
        Map<Integer, Integer> map = new HashMap<>();
        for (RoomType rt : getAvailableRoomTypes()) {
            map.put(rt.getRoomTypeId(), getAvailableCount(rt.getRoomTypeId(), checkIn, checkOut));
        }
        return map;
    }

    @Transactional
    public RoomType save(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    @Transactional
    public void delete(Integer id) {
        roomTypeRepository.deleteById(id);
    }
}