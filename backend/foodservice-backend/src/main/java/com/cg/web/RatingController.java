package com.cg.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.cg.entity.Rating;
import com.cg.service.RatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;
    
    @PostMapping
    public ResponseEntity<Rating> addRating(
            @Valid @RequestBody RatingDTO dto) {

        Rating savedRating = ratingService.addRating(dto);
        return new ResponseEntity<>(savedRating, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRatingById(@PathVariable Integer id) {
        return ResponseEntity.ok(ratingService.getRatingById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Rating>> getRatingsByRestaurant(
            @PathVariable Integer restaurantId) {

        return ResponseEntity.ok(
                ratingService.getRatingsByRestaurant(restaurantId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rating> updateRating(
            @PathVariable Integer id,
            @Valid @RequestBody RatingDTO dto) {

        return ResponseEntity.ok(
                ratingService.updateRating(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRating(@PathVariable Integer id) {
        ratingService.deleteRating(id);
        return ResponseEntity.ok("Rating deleted successfully");
    }
}