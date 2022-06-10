package com.test.persistence.services;

import com.test.persistence.entities.NNPreview;
import com.test.persistence.repositories.NNPreviewRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
public class NNPreviewService {

    private final NNPreviewRepository nnPreviewRepository;

    public NNPreviewService(NNPreviewRepository nnPreviewRepository) {
        this.nnPreviewRepository = nnPreviewRepository;
    }

    public NNPreview save(ByteArrayOutputStream imagePreview) {
        NNPreview nnPreview = new NNPreview()
                .setDate(LocalDateTime.now())
                .setPreviewImage(imagePreview.toByteArray());
        return nnPreviewRepository.save(nnPreview);
    }
}
