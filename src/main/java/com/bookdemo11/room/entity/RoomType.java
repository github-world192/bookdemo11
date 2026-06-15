package com.bookdemo11.room.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_type")
@Getter
@Setter
@NoArgsConstructor
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id")
    private Integer roomTypeId;

    @NotBlank
    @Column(name = "room_type_name", nullable = false)
    private String roomTypeName;

    @Column(name = "room_type_content", length = 2000)
    private String roomTypeContent;

    @NotNull
    @Min(1)
    @Column(name = "room_type_price", nullable = false)
    private Integer roomTypePrice;

    @NotNull
    @Min(1)
    @Column(name = "guest_num", nullable = false)
    private Integer guestNum;

    @Column(name = "room_sale_status")
    private Byte roomSaleStatus = 1;

    @Lob
    @Column(name = "room_type_pic")
    private byte[] roomTypePic;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();
}