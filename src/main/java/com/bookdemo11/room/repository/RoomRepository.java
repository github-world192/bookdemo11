package com.bookdemo11.room.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.room.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByRoomTypeRoomTypeId(Integer roomTypeId);
    long countByRoomTypeRoomTypeIdAndRoomStatus(Integer roomTypeId, Integer roomStatus);
}