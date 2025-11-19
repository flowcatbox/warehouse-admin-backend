package com.warehouse.service;

import com.warehouse.entity.LinenItem;
import com.warehouse.repository.LinenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LinenService {
    private final LinenRepository linenRepository;

    public Optional<LinenItem> getLinenById(Long id){
        return linenRepository.findById(id);
    }

    public LinenItem createLinen(LinenItem linenItemInfo){
        linenItemInfo.setCreatedAt(LocalDateTime.now());
        return linenRepository.save(linenItemInfo);
    }

    public boolean deleteLinen(Long id) {
        if(linenRepository.findById(id).isPresent()) {
            linenRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
