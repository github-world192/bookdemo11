package com.bookdemo11.room.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookdemo11.room.entity.Room;
import com.bookdemo11.room.entity.RoomType;
import com.bookdemo11.room.repository.RoomRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findByRoomTypeId(Integer roomTypeId) {
        return roomRepository.findByRoomTypeRoomTypeId(roomTypeId);
    }

    public Optional<Room> findById(Integer id) {
        return roomRepository.findById(id);
    }

    @Transactional
    public Room save(Room room, RoomType roomType) {
        room.setRoomType(roomType);
        return roomRepository.save(room);
    }

    @Transactional
    public void delete(Integer id) {
        roomRepository.deleteById(id);
    }
}