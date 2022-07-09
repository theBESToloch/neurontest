package com.test.persistence.services;

import com.test.data.NeuronGraph;
import com.test.persistence.entities.NNDescription;
import com.test.persistence.entities.NNPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NNDescriptionService {
    Page<NNDescription> load(Pageable pageable);
    NNDescription save(List<NeuronGraph> struct, NNPreview nnPreview);
    void delete(Long id);
}
