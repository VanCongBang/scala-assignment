-- !Ups
create table testing.users
(
    id           serial       not null
        constraint users_pk primary key,
    email        varchar(100) not null,
    first_name   varchar(64)  not null,
    last_Name    varchar(64)  not null,
    password     varchar(128) not null,
    role         varchar(16)  not null,
    birth_date   timestamp    not null,
    address      varchar(200) not null,
    phone_number varchar(64)  not null
);

create unique index users_id_uindex
    on testing.users (id);

create table testing.orders
(
    id          serial         not null
        constraint orders_pk primary key,
    user_id     serial,
    order_date  timestamp      not null,
    total_price numeric(12, 2) not null,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES testing.users (id)
);

create unique index orders_id_uindex
    on testing.orders (id);

create table testing.products
(
    id           serial         not null
        constraint products_pk primary key,
    product_name varchar(100)   not null,
    price        numeric(12, 2) not null,
    exp_date     varchar(100)   not null
);

create unique index products_id_uindex
    on testing.products (id);

create table testing.orderDetails
(
    id         serial         not null
        constraint orderDetails_pk primary key,
    order_id   serial,
    product_id serial,
    quantity   numeric(10)    not null,
    price      numeric(12, 2) not null,
    CONSTRAINT fk_order
        FOREIGN KEY (order_id)
            REFERENCES testing.orders (id),
    CONSTRAINT fk_product
        FOREIGN KEY (product_id)
            REFERENCES testing.products (id)
);

create unique index orderDetails_id_uindex
    on testing.orderDetails (id);

-- !Downs
DROP TABLE testing.users cascade;
DROP TABLE testing.products cascade;
DROP TABLE testing.orderDetails cascade;
DROP TABLE testing.orders cascade;
