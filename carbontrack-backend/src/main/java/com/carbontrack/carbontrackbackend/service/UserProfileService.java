
package com.carbontrack.carbontrackbackend.service;

import com.carbontrack.carbontrackbackend.dto.UpdateProfileRequestDTO;
import com.carbontrack.carbontrackbackend.dto.UserProfileResponseDTO;
import com.carbontrack.carbontrackbackend.entity.User;
import com.carbontrack.carbontrackbackend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserProfileService {

    private final UserRepository userRepository;

    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDTO getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return convertToDTO(user);
    }

    @Transactional
    public UserProfileResponseDTO updateMyProfile(
            String email,
            UpdateProfileRequestDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        String requestedUsername = request.getUsername().trim();

        if (!requestedUsername.equals(user.getUsername())
                && userRepository.existsByUsername(requestedUsername)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username is already taken"
            );
        }

        user.setUsername(requestedUsername);
        user.setPreferredUnit(request.getPreferredUnit());
        user.setGoalVisibility(request.isGoalVisibility());

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    private UserProfileResponseDTO convertToDTO(User user) {
        return new UserProfileResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getPreferredUnit(),
                user.getGoalVisibility()
        );
    }
}
