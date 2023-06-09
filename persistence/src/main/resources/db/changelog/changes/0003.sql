alter table NNDescription
    ADD COLUMN nnPreviewId bigserial,
    ADD FOREIGN KEY (nnPreviewId) REFERENCES NNPreview (id);
