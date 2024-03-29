create table animal (id bigint not null, description varchar(255), image varchar(255), name varchar(255), primary key (id)) engine=InnoDB;
create table animal_seq (next_val bigint) engine=InnoDB;
insert into animal_seq values ( 1 );
create table cart (added_time bigint, id bigint not null, product_id bigint, quantity bigint, user_id bigint, primary key (id)) engine=InnoDB;
create table cart_seq (next_val bigint) engine=InnoDB;
insert into cart_seq values ( 1 );
create table category (animal_id bigint, id bigint not null, description varchar(255), image varchar(255), name varchar(255), primary key (id)) engine=InnoDB;
create table category_seq (next_val bigint) engine=InnoDB;
insert into category_seq values ( 1 );
create table color_variant_seq (next_val bigint) engine=InnoDB;
insert into color_variant_seq values ( 1 );
create table color_variant (id bigint not null, product_id bigint, variant_id bigint, primary key (id)) engine=InnoDB;
create table order_history_seq (next_val bigint) engine=InnoDB;
insert into order_history_seq values ( 1 );
create table order_history (id bigint not null, user_id bigint, primary key (id)) engine=InnoDB;
create table product (is_visible bit, to_be_deleted bit, weight integer, weight_units integer, category_id bigint, id bigint not null, price bigint, stocks bigint, variant_color_id bigint, variant_size_id bigint, variant_weight_id bigint, color varchar(255), description varchar(255), details varchar(255), name varchar(255), size varchar(255), primary key (id)) engine=InnoDB;
create table product_seq (next_val bigint) engine=InnoDB;
insert into product_seq values ( 1 );
create table size_variant_seq (next_val bigint) engine=InnoDB;
insert into size_variant_seq values ( 1 );
create table size_variant (id bigint not null, product_id bigint, variant_id bigint, primary key (id)) engine=InnoDB;
create table top_categories_seq (next_val bigint) engine=InnoDB;
insert into top_categories_seq values ( 1 );
create table top_products_seq (next_val bigint) engine=InnoDB;
insert into top_products_seq values ( 1 );
create table top_categories (order_number integer, category_id bigint, id bigint not null, product_id bigint, primary key (id)) engine=InnoDB;
create table top_products (order_number integer, id bigint not null, product_id bigint, primary key (id)) engine=InnoDB;
create table user (id bigint not null, address varchar(255), city varchar(255), country varchar(255), email varchar(255), name varchar(255), password varchar(255), phone_extension varchar(255), phone_number varchar(255), pincode varchar(255), state varchar(255), primary key (id)) engine=InnoDB;
create table user_seq (next_val bigint) engine=InnoDB;
insert into user_seq values ( 1 );
create table weight_variant_seq (next_val bigint) engine=InnoDB;
insert into weight_variant_seq values ( 1 );
create table weight_variant (id bigint not null, product_id bigint, variant_id bigint, primary key (id)) engine=InnoDB;
alter table cart add constraint UK_9emlp6m95v5er2bcqkjsw48he unique (user_id);
alter table color_variant add constraint UK_c8n7l0pby4xaer05fwxkyuu63 unique (product_id);
alter table size_variant add constraint UK_d8l6kqlyg8xgjf2bp7ivvbo60 unique (product_id);
alter table top_categories add constraint UK_5t3qkfcyx32r4v6h3jka0bfdw unique (category_id);
alter table top_categories add constraint UK_qj9avxyf10fs48h36s8wag0ku unique (product_id);
alter table top_products add constraint UK_hr59u0eqirvhi9ii25btoyhce unique (product_id);
alter table weight_variant add constraint UK_rfyyucj5uiu1rcagtl0076bq0 unique (product_id);
alter table cart add constraint FK3d704slv66tw6x5hmbm6p2x3u foreign key (product_id) references product (id);
alter table cart add constraint FKl70asp4l4w0jmbm1tqyofho4o foreign key (user_id) references user (id);
alter table category add constraint FK3c5ntafj4uo6e3rpeg2n5afud foreign key (animal_id) references animal (id);
alter table color_variant add constraint FKoau75vt437qxct815kw0brwdx foreign key (product_id) references product (id);
alter table order_history add constraint FKp03guo9hm9uf9k0n4a1sam969 foreign key (user_id) references user (id);
alter table product add constraint FK1mtsbur82frn64de7balymq9s foreign key (category_id) references category (id);
alter table size_variant add constraint FKt8pru6o77else1vtxogpdi127 foreign key (product_id) references product (id);
alter table top_categories add constraint FKeb3c9wegy4b6dx6r4ykc4kw6k foreign key (category_id) references category (id);
alter table top_categories add constraint FKml5yck36r7ot4c95ry5nonq31 foreign key (product_id) references product (id);
alter table top_products add constraint FKjq7e949ywjv82p0nvdj0s9isq foreign key (product_id) references product (id);
alter table weight_variant add constraint FK9exgn7kmovcihcmltldbrcytf foreign key (product_id) references product (id);
create table animal (id bigint not null, description varchar(255), image varchar(255), name varchar(255), primary key (id)) engine=InnoDB;
create table animal_seq (next_val bigint) engine=InnoDB;
insert into animal_seq values ( 1 );
create table cart (added_time bigint, id bigint not null, product_id bigint, quantity bigint, user_id bigint, primary key (id)) engine=InnoDB;
create table cart_seq (next_val bigint) engine=InnoDB;
insert into cart_seq values ( 1 );
create table category (animal_id bigint, id bigint not null, description varchar(255), image varchar(255), name varchar(255), primary key (id)) engine=InnoDB;
create table category_seq (next_val bigint) engine=InnoDB;
insert into category_seq values ( 1 );
create table color_variant_seq (next_val bigint) engine=InnoDB;
insert into color_variant_seq values ( 1 );
create table color_variant (id bigint not null, product_id bigint, variant_id bigint, primary key (id)) engine=InnoDB;
create table order_history_seq (next_val bigint) engine=InnoDB;
insert into order_history_seq values ( 1 );
create table order_history (id bigint not null, user_id bigint, primary key (id)) engine=InnoDB;
create table product (is_visible bit, to_be_deleted bit, weight integer, weight_units integer, category_id bigint, id bigint not null, price bigint, stocks bigint, variant_color_id bigint, variant_size_id bigint, variant_weight_id bigint, color varchar(255), description varchar(255), details varchar(255), name varchar(255), size varchar(255), primary key (id)) engine=InnoDB;
create table product_seq (next_val bigint) engine=InnoDB;
insert into product_seq values ( 1 );
create table size_variant_seq (next_val bigint) engine=InnoDB;
insert into size_variant_seq values ( 1 );
create table size_variant (id bigint not null, product_id bigint, variant_id bigint, primary key (id)) engine=InnoDB;
create table top_categories_seq (next_val bigint) engine=InnoDB;
insert into top_categories_seq values ( 1 );
create table top_products_seq (next_val bigint) engine=InnoDB;
insert into top_products_seq values ( 1 );
create table top_categories (order_number integer, category_id bigint, id bigint not null, product_id bigint, primary key (id)) engine=InnoDB;
create table top_products (order_number integer, id bigint not null, product_id bigint, primary key (id)) engine=InnoDB;
create table user (id bigint not null, address varchar(255), city varchar(255), country varchar(255), email varchar(255), name varchar(255), password varchar(255), phone_extension varchar(255), phone_number varchar(255), pincode varchar(255), state varchar(255), primary key (id)) engine=InnoDB;
create table user_seq (next_val bigint) engine=InnoDB;
insert into user_seq values ( 1 );
create table weight_variant_seq (next_val bigint) engine=InnoDB;
insert into weight_variant_seq values ( 1 );
create table weight_variant (id bigint not null, product_id bigint, variant_id bigint, primary key (id)) engine=InnoDB;
alter table cart add constraint UK_9emlp6m95v5er2bcqkjsw48he unique (user_id);
alter table color_variant add constraint UK_c8n7l0pby4xaer05fwxkyuu63 unique (product_id);
alter table size_variant add constraint UK_d8l6kqlyg8xgjf2bp7ivvbo60 unique (product_id);
alter table top_categories add constraint UK_5t3qkfcyx32r4v6h3jka0bfdw unique (category_id);
alter table top_categories add constraint UK_qj9avxyf10fs48h36s8wag0ku unique (product_id);
alter table top_products add constraint UK_hr59u0eqirvhi9ii25btoyhce unique (product_id);
alter table weight_variant add constraint UK_rfyyucj5uiu1rcagtl0076bq0 unique (product_id);
alter table cart add constraint FK3d704slv66tw6x5hmbm6p2x3u foreign key (product_id) references product (id);
alter table cart add constraint FKl70asp4l4w0jmbm1tqyofho4o foreign key (user_id) references user (id);
alter table category add constraint FK3c5ntafj4uo6e3rpeg2n5afud foreign key (animal_id) references animal (id);
alter table color_variant add constraint FKoau75vt437qxct815kw0brwdx foreign key (product_id) references product (id);
alter table order_history add constraint FKp03guo9hm9uf9k0n4a1sam969 foreign key (user_id) references user (id);
alter table product add constraint FK1mtsbur82frn64de7balymq9s foreign key (category_id) references category (id);
alter table size_variant add constraint FKt8pru6o77else1vtxogpdi127 foreign key (product_id) references product (id);
alter table top_categories add constraint FKeb3c9wegy4b6dx6r4ykc4kw6k foreign key (category_id) references category (id);
alter table top_categories add constraint FKml5yck36r7ot4c95ry5nonq31 foreign key (product_id) references product (id);
alter table top_products add constraint FKjq7e949ywjv82p0nvdj0s9isq foreign key (product_id) references product (id);
alter table weight_variant add constraint FK9exgn7kmovcihcmltldbrcytf foreign key (product_id) references product (id);
