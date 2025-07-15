package com.digitaltwin.backend.service;

import com.digitaltwin.backend.model.TwinProfile;
import com.digitaltwin.backend.repository.TwinProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TwinProfileService {

    @Autowired
    private TwinProfileRepository twinProfileRepository;

    public TwinProfile save(TwinProfile profile) {
        return twinProfileRepository.save(profile);
    }

    public Optional<TwinProfile> findByUserId(String userId) {
        return twinProfileRepository.findByUserId(userId);
    }

    public void deleteById(String id) {
        twinProfileRepository.deleteById(id);
    }
}
