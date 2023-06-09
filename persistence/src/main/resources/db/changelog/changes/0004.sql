alter table NNDescription
    ADD CONSTRAINT nnPreviewId FOREIGN KEY (nnPreviewId) REFERENCES NNPreview (id) on DELETE cascade;

