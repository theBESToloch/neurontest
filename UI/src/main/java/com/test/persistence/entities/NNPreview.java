package com.test.persistence.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.springframework.context.annotation.Lazy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "nnpreview")
public class NNPreview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date", columnDefinition = "timestamp")
    private LocalDateTime date;

    @Column(name = "name", columnDefinition = "varchar(100)")
    private String name;

    @Column(name = "description", columnDefinition = "varchar(2000)")
    private String description;

    @Lob
    @Column(name = "preview", columnDefinition = "bytea")
    @Type(type = "org.hibernate.type.BinaryType")
    @Lazy
    private byte[] previewImage;

    @OneToOne(mappedBy = "nnPreview")
    private NNDescription nnDescription;
}
