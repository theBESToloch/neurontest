create table NNPreview
(
    id              bigserial not null primary key,
    date            timestamp,
    name            varchar(100),
    description     varchar(2000),
    preview         bytea
)