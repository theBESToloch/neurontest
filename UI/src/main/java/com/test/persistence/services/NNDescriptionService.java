package com.test.persistence.services;

import com.test.data.NeuronGraph;
import com.test.persistence.entities.NNDescription;
import com.test.persistence.entities.NNPreview;
import com.test.persistence.repositories.NNDescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NNDescriptionService {

    private final NNDescriptionRepository nnDescriptionRepository;

    public NNDescriptionService(NNDescriptionRepository nnDescriptionRepository) {
        this.nnDescriptionRepository = nnDescriptionRepository;
    }

    public List<NNDescription> load() {
        return nnDescriptionRepository.findAll();
    }

    public NNDescription save(List<NeuronGraph> struct, NNPreview nnPreview) {
        return nnDescriptionRepository.saveAndFlush(new NNDescription()
                .setStruct(struct)
                .setNnPreview(nnPreview));
    }

    public void delete(Long id) {
        nnDescriptionRepository.deleteById(id);
    }
}
