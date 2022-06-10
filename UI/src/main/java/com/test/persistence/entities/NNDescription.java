package com.test.persistence.entities;

import com.test.data.NeuronGraph;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;


@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonBinaryType.class)
})
@Entity
@Table(name = "nndescription")
public class NNDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Type(type = "json")
    @Column(name = "struct", columnDefinition = "json")
    private List<NeuronGraph> struct;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nnpreviewid", referencedColumnName = "id")
    private NNPreview nnPreview;
}
