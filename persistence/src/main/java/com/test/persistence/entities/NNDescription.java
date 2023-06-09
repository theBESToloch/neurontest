package com.test.persistence.entities;

import com.test.common.data.dto.NeuronGraph;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "nndescription")
public class NNDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "struct", columnDefinition = "json")
    private String struct;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nnpreviewid", referencedColumnName = "id")
    private NNPreview nnPreview;
}
