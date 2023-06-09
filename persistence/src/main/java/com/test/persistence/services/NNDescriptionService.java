package com.test.persistence.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.common.data.dto.NeuronGraph;
import com.test.persistence.entities.NNDescription;
import com.test.persistence.entities.NNPreview;
import com.test.persistence.repositories.NNDescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NNDescriptionService {

    private final NNDescriptionRepository nnDescriptionRepository;
    private final ObjectMapper objectMapper;

    public Page<NNDescription> load(Pageable pageable) {
        return nnDescriptionRepository.findAll(pageable);
    }

    @SneakyThrows
    public NNDescription save(List<NeuronGraph> struct, NNPreview nnPreview) {
        return nnDescriptionRepository.save(new NNDescription()
                .setStruct(objectMapper.writeValueAsString(struct))
                .setNnPreview(nnPreview));
    }

    public void delete(Long id) {
        nnDescriptionRepository.deleteById(id);
    }
}
