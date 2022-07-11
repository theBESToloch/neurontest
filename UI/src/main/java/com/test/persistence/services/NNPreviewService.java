package com.test.persistence.services;

import com.test.persistence.entities.NNPreview;
import com.test.persistence.repositories.NNPreviewRepository;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

public interface NNPreviewService {
    NNPreview save(ByteArrayOutputStream imagePreview);
}