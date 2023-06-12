package com.test.persistence.services;

import com.test.persistence.entities.NNPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.io.ByteArrayOutputStream;

public interface NNPreviewService {
    NNPreview save(ByteArrayOutputStream imagePreview);
    Page<NNPreview> load(Pageable pageable);
    public void delete(Long id);
}