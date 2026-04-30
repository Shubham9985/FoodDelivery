package com.cg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cg.dto.RatingDTO;
import com.cg.service.RatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingService service;

    @PostMapping
    public ResponseEntity<RatingDTO> add(@Valid @RequestBody RatingDTO dto) {
        return ResponseEntity.ok(service.addRating(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getRatingById(id));
    }

    @GetMapping
    public ResponseEntity<List<RatingDTO>> getAll() {
        return ResponseEntity.ok(service.getAllRatings());
    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<List<RatingDTO>> getByRestaurant(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getRatingsByRestaurant(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDTO> update(@PathVariable Integer id,
                                           @Valid @RequestBody RatingDTO dto) {
        return ResponseEntity.ok(service.updateRating(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        service.deleteRating(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/average/{restaurantId}")
    public ResponseEntity<Double> avg(@PathVariable Integer restaurantId) {
        return ResponseEntity.ok(service.getAverageRating(restaurantId));
    }
}