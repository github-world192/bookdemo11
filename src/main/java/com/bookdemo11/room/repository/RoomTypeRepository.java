package com.bookdemo11.room.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookdemo11.room.entity.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
    List<RoomType> findByRoomSaleStatus(Byte roomSaleStatus);
}