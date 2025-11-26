package com.work.pamper.controller;

import com.work.pamper.entity.PetProfile;
import com.work.pamper.entity.HealthRecord;
import com.work.pamper.service.PetService;
import com.work.pamper.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping("/upload/avatar")
    public Object uploadPetAvatar(@RequestParam("file") MultipartFile file,
                                  @RequestHeader("Authorization") String token) {
        return FileUtils.uploadPetAvatar(file, token);
    }

    @GetMapping("/avatar/{filename}")
    public ResponseEntity<byte[]> showPetAvatar(@PathVariable String filename) {
        return FileUtils.showPetAvatar(filename);
    }

    @PostMapping("/profile/create")
    public Object createPetProfile(@RequestHeader("Authorization") String token,
                                   @RequestBody PetProfile petProfile) {
        return petService.createPetProfile(token, petProfile);
    }

    @GetMapping("/profile/{id:\\d+}")
    public Object getPetProfile(@RequestHeader("Authorization") String token,
                                @PathVariable Long id) {
        return petService.getPetProfile(token, id);
    }

    @GetMapping("/profile/my")
    public Object getMyPets(@RequestHeader("Authorization") String token) {
        return petService.getMyPets(token);
    }

    @PutMapping("/profile/update")
    public Object updatePetProfile(@RequestHeader("Authorization") String token,
                                   @RequestBody PetProfile petProfile) {
        return petService.updatePetProfile(token, petProfile);
    }

    @DeleteMapping("/profile/{id:\\d+}")
    public Object deletePetProfile(@RequestHeader("Authorization") String token,
                                   @PathVariable Long id) {
        return petService.deletePetProfile(token, id);
    }

    @PostMapping("/health/add")
    public Object addHealthRecord(@RequestHeader("Authorization") String token,
                                  @RequestBody HealthRecord record) {
        return petService.addHealthRecord(token, record);
    }

    @GetMapping("/health/{petId:\\d+}")
    public Object getHealthRecords(@RequestHeader("Authorization") String token,
                                   @PathVariable Long petId) {
        return petService.getHealthRecords(token, petId);
    }

    @PutMapping("/health/update")
    public Object updateHealthRecord(@RequestHeader("Authorization") String token,
                                     @RequestBody HealthRecord record) {
        return petService.updateHealthRecord(token, record);
    }

    @DeleteMapping("/health/{id:\\d+}")
    public Object deleteHealthRecord(@RequestHeader("Authorization") String token,
                                     @PathVariable Long id) {
        return petService.deleteHealthRecord(token, id);
    }
}
