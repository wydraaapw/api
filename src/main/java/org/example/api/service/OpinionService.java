package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.OpinionRequest;
import org.example.api.dto.OpinionResponse;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.exception.UserNotFoundException;
import org.example.api.model.Client;
import org.example.api.model.Opinion;
import org.example.api.model.User;
import org.example.api.repository.ClientRepository;
import org.example.api.repository.OpinionRepository;
import org.example.api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpinionService {
    private final OpinionRepository opinionRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public void createOpinion(OpinionRequest opinionRequest){
        Client client = clientRepository.findById(opinionRequest.clientId())
                .orElseThrow(() -> new UserNotFoundException("Klient o id: " + opinionRequest.clientId() + " nie istnieje"));

        Opinion opinion = Opinion.builder()
                .content(opinionRequest.content())
                .createdAt(opinionRequest.createdAt())
                .client(client)
                .rating(opinionRequest.rating())
                .build();

        opinionRepository.save(opinion);
    }

    public Page<OpinionResponse> getAllOpinions(Pageable pageable){
        return opinionRepository.findAll(pageable).map(this::mapToResponse);
    }

    public void deleteOpinion(Long id){
        if (!opinionRepository.existsById(id)){
            throw new ResourceNotFoundException("Opinia o id: " + id + " nie istnieje.");
        }

        opinionRepository.deleteById(id);
    }

    private OpinionResponse mapToResponse(Opinion opinion){

        User user = userRepository.findById(opinion.getClient().getId())
                .orElseThrow(() -> new UserNotFoundException("Użytkownik o id: " + opinion.getClient().getId() + " nie istnieje"));

        return new OpinionResponse(
                opinion.getId(),
                opinion.getContent(),
                opinion.getCreatedAt(),
                user.getFirstName(),
                user.getLastName(),
                opinion.getRating()
        );
    }
}
