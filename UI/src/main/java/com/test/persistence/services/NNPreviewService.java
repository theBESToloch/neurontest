package com.test.persistence.services;

import com.test.persistence.entities.NNPreview;


import java.io.ByteArrayOutputStream;

public interface NNPreviewService {
    NNPreview save(ByteArrayOutputStream imagePreview);
}