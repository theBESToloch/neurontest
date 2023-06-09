package com.test.persistence.services;

import com.test.persistence.entities.NNPreview;
import com.test.persistence.repositories.NNPreviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
public class NNPreviewServiceImpl implements NNPreviewService {

    private final NNPreviewRepository nnPreviewRepository;

    public NNPreviewServiceImpl(NNPreviewRepository nnPreviewRepository) {
        this.nnPreviewRepository = nnPreviewRepository;
    }

    public NNPreview save(ByteArrayOutputStream imagePreview) {
        NNPreview nnPreview = new NNPreview()
                .setDate(LocalDateTime.now())
                .setPreviewImage(imagePreview.toByteArray());
        return nnPreviewRepository.save(nnPreview);
    }

    @Override
    public Page<NNPreview> load(Pageable pageable) {
        return  nnPreviewRepository.findAll(pageable);
    }

    @Override
    public void delete(Long id) {
        nnPreviewRepository.deleteById(id);

    }
}
