package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.OpinionRequest;
import org.example.api.dto.OpinionResponse;
import org.example.api.service.OpinionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/opinions")
public class OpinionController {

    private final OpinionService opinionService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> addOpinion(@Valid @RequestBody OpinionRequest opinionRequest){
        opinionService.createOpinion(opinionRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<OpinionResponse>> getOpinions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(opinionService.getAllOpinions(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOpinion(@PathVariable Long id){
        opinionService.deleteOpinion(id);
        return ResponseEntity.noContent().build();
    }

}
