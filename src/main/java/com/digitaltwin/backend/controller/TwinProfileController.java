package com.digitaltwin.backend.controller;

import com.digitaltwin.backend.model.TwinProfile;
import com.digitaltwin.backend.service.TwinProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/twin/profile")
@CrossOrigin
public class TwinProfileController {

    @Autowired
    private TwinProfileService twinProfileService;

    @PostMapping
    public TwinProfile twinProfile(@RequestBody TwinProfile profile) {
        return twinProfileService.save(profile);
    }

    @GetMapping("/{userId}")
    public TwinProfile getByUserId(@PathVariable String userId) {
        return twinProfileService.findByUserId(userId).orElse(null);
    }
}
