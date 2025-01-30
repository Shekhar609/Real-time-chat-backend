package com.substring.chat.controllers;

import com.substring.chat.entities.Message;
import com.substring.chat.entities.Room;
import com.substring.chat.repositries.RoomRepository;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Create room
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody String roomId) {
        if (roomRepository.findByRoomId(roomId) != null) {
            return ResponseEntity.badRequest().body("Room already exists");
        }

        Room room = new Room();
        room.setRoomId(roomId);
        Room savedRoom = roomRepository.save(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    // Get room by ID
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable String roomId) {
        Optional<Room> room = Optional.ofNullable(roomRepository.findByRoomId(roomId));
        if (room.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }
        return ResponseEntity.ok(room.get());
    }

    //get message of room
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable String roomId,
            @RequestParam(value = "page",defaultValue = "0",required = false) int page,
            @RequestParam(value = "size",defaultValue ="20",required = false ) int size)
    {
        Room room = roomRepository.findByRoomId(roomId);
        if(room==null)
        {
            return ResponseEntity.badRequest().build();
        }
        List<Message> message = room.getMessages();
        int start = Math.max(0,message.size()-(page+1)*size);
        int end= Math.max(message.size(),start+size);
        List<Message> paginatedMessage = message.subList(start,end);


        return ResponseEntity.ok(paginatedMessage);

    }



}
