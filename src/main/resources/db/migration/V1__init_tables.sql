
CREATE TABLE public.address (
    address_id bigint NOT NULL,
    member_id bigint NOT NULL,
    recipient character varying(50) NOT NULL,
    phone character varying(20) NOT NULL,
    address character varying(255) NOT NULL,
    address_detail character varying(255),
    postal_code character varying(10) NOT NULL,
    default_address boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted_at timestamp without time zone,
    request_message text
);

CREATE SEQUENCE public.address_address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.address_address_id_seq OWNED BY public.address.address_id;

CREATE TABLE public.cart_item (
    cart_item_id bigint NOT NULL,
    member_id bigint NOT NULL,
    product_option_id bigint NOT NULL,
    quantity bigint DEFAULT 1 NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT cart_item_quantity_check CHECK ((quantity > 0))
);

CREATE SEQUENCE public.cart_item_cart_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.cart_item_cart_item_id_seq OWNED BY public.cart_item.cart_item_id;

CREATE TABLE public.category (
    category_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted_at timestamp without time zone
);

CREATE SEQUENCE public.category_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.category_category_id_seq OWNED BY public.category.category_id;

CREATE TABLE public.image (
    image_id bigint NOT NULL,
    owner_id bigint NOT NULL,
    owner_type character varying(20) NOT NULL,
    image_url character varying(255) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted_at timestamp without time zone,
    CONSTRAINT image_owner_type_check CHECK (((owner_type)::text = ANY (ARRAY[('MEMBER'::character varying)::text, ('PRODUCT'::character varying)::text, ('REVIEW'::character varying)::text, ('BANNER'::character varying)::text])))
);

CREATE SEQUENCE public.image_image_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.image_image_id_seq OWNED BY public.image.image_id;

CREATE TABLE public.member (
    member_id bigint NOT NULL,
    email character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    name character varying(50) NOT NULL,
    role character varying(20) DEFAULT 'USER'::character varying NOT NULL,
    is_term boolean DEFAULT true NOT NULL,
    is_marketing boolean DEFAULT false NOT NULL,
    profile_url character varying(255),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted_at timestamp without time zone,
    phone character varying(13),
    CONSTRAINT member_role_check CHECK (((role)::text = ANY (ARRAY[('USER'::character varying)::text, ('ADMIN'::character varying)::text])))
);

CREATE SEQUENCE public.member_member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.member_member_id_seq OWNED BY public.member.member_id;

CREATE TABLE public.option_key (
    option_key_id bigint NOT NULL,
    product_id bigint NOT NULL,
    name character varying(100) NOT NULL
);

CREATE SEQUENCE public.option_key_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.option_key_id_seq OWNED BY public.option_key.option_key_id;

CREATE TABLE public.option_value (
    option_value_id bigint NOT NULL,
    option_key_id bigint NOT NULL,
    value character varying(100) NOT NULL
);

CREATE SEQUENCE public.option_value_option_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.option_value_option_value_id_seq OWNED BY public.option_value.option_value_id;

CREATE TABLE public.option_variant (
    option_variant_id bigint NOT NULL,
    product_option_id bigint NOT NULL,
    option_value_id bigint NOT NULL
);

CREATE SEQUENCE public.option_variant_option_variant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.option_variant_option_variant_id_seq OWNED BY public.option_variant.option_variant_id;

CREATE TABLE public.order_item (
    order_item_id bigint NOT NULL,
    order_id bigint NOT NULL,
    product_option_id bigint NOT NULL,
    quantity bigint NOT NULL,
    price numeric(10,2) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT order_item_quantity_check CHECK ((quantity > 0))
);

CREATE SEQUENCE public.order_item_order_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.order_item_order_item_id_seq OWNED BY public.order_item.order_item_id;

CREATE TABLE public.orders (
    order_id bigint NOT NULL,
    member_id bigint NOT NULL,
    address_id bigint NOT NULL,
    total_amount numeric(10,2) NOT NULL,
    status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted_at timestamp without time zone,
    CONSTRAINT orders_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('PAID'::character varying)::text, ('SHIPPING'::character varying)::text, ('COMPLETED'::character varying)::text, ('CANCELLED'::character varying)::text])))
);

CREATE SEQUENCE public.orders_order_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.orders_order_id_seq OWNED BY public.orders.order_id;

CREATE TABLE public.payment (
    payment_id bigint NOT NULL,
    order_id bigint NOT NULL,
    member_id bigint NOT NULL,
    order_uid character varying(50) NOT NULL,
    payment_key character varying(100),
    amount numeric(10,2) NOT NULL,
    currency character varying(10) DEFAULT 'KRW'::character varying NOT NULL,
    method character varying(30) NOT NULL,
    pg_provider character varying(50),
    transaction_id character varying(100),
    status character varying(20) DEFAULT 'READY'::character varying NOT NULL,
    approved_at timestamp without time zone,
    canceled_at timestamp without time zone,
    failed_at timestamp without time zone,
    fail_reason text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE public.payment_payment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.payment_payment_id_seq OWNED BY public.payment.payment_id;

CREATE TABLE public.product (
    product_id bigint NOT NULL,
    category_id bigint NOT NULL,
    name character varying(100) NOT NULL,
    description text,
    price numeric(10,2) NOT NULL,
    thumbnail_url character varying(255),
    created_by bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted_at timestamp without time zone
);

CREATE TABLE public.product_option (
    product_option_id bigint NOT NULL,
    product_id bigint NOT NULL,
    sku character varying(50) NOT NULL,
    additional_price numeric(10,2) DEFAULT 0,
    stock bigint DEFAULT 0,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT product_option_additional_price_check CHECK ((additional_price >= (0)::numeric)),
    CONSTRAINT product_option_stock_check CHECK ((stock >= 0))
);

CREATE SEQUENCE public.product_option_product_option_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.product_option_product_option_id_seq OWNED BY public.product_option.product_option_id;

CREATE SEQUENCE public.product_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.product_product_id_seq OWNED BY public.product.product_id;

CREATE TABLE public.wishlist (
    wishlist_id bigint NOT NULL,
    member_id bigint NOT NULL,
    product_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT now()
);

CREATE SEQUENCE public.wishlist_wishlist_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.wishlist_wishlist_id_seq OWNED BY public.wishlist.wishlist_id;

ALTER TABLE ONLY public.address ALTER COLUMN address_id SET DEFAULT nextval('public.address_address_id_seq'::regclass);

ALTER TABLE ONLY public.cart_item ALTER COLUMN cart_item_id SET DEFAULT nextval('public.cart_item_cart_item_id_seq'::regclass);

ALTER TABLE ONLY public.category ALTER COLUMN category_id SET DEFAULT nextval('public.category_category_id_seq'::regclass);

ALTER TABLE ONLY public.image ALTER COLUMN image_id SET DEFAULT nextval('public.image_image_id_seq'::regclass);

ALTER TABLE ONLY public.member ALTER COLUMN member_id SET DEFAULT nextval('public.member_member_id_seq'::regclass);

ALTER TABLE ONLY public.option_key ALTER COLUMN option_key_id SET DEFAULT nextval('public.option_key_id_seq'::regclass);

ALTER TABLE ONLY public.option_value ALTER COLUMN option_value_id SET DEFAULT nextval('public.option_value_option_value_id_seq'::regclass);

ALTER TABLE ONLY public.option_variant ALTER COLUMN option_variant_id SET DEFAULT nextval('public.option_variant_option_variant_id_seq'::regclass);

ALTER TABLE ONLY public.order_item ALTER COLUMN order_item_id SET DEFAULT nextval('public.order_item_order_item_id_seq'::regclass);

ALTER TABLE ONLY public.orders ALTER COLUMN order_id SET DEFAULT nextval('public.orders_order_id_seq'::regclass);

ALTER TABLE ONLY public.payment ALTER COLUMN payment_id SET DEFAULT nextval('public.payment_payment_id_seq'::regclass);

ALTER TABLE ONLY public.product ALTER COLUMN product_id SET DEFAULT nextval('public.product_product_id_seq'::regclass);

ALTER TABLE ONLY public.product_option ALTER COLUMN product_option_id SET DEFAULT nextval('public.product_option_product_option_id_seq'::regclass);

ALTER TABLE ONLY public.wishlist ALTER COLUMN wishlist_id SET DEFAULT nextval('public.wishlist_wishlist_id_seq'::regclass);

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);

ALTER TABLE ONLY public.cart_item
    ADD CONSTRAINT cart_item_pkey PRIMARY KEY (cart_item_id);

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);

ALTER TABLE ONLY public.image
    ADD CONSTRAINT image_pkey PRIMARY KEY (image_id);

ALTER TABLE ONLY public.member
    ADD CONSTRAINT member_email_key UNIQUE (email);

ALTER TABLE ONLY public.member
    ADD CONSTRAINT member_pkey PRIMARY KEY (member_id);

ALTER TABLE ONLY public.option_key
    ADD CONSTRAINT option_key_pkey PRIMARY KEY (option_key_id);

ALTER TABLE ONLY public.option_key
    ADD CONSTRAINT option_key_product_id_name_key UNIQUE (product_id, name);

ALTER TABLE ONLY public.option_value
    ADD CONSTRAINT option_value_pkey PRIMARY KEY (option_value_id);

ALTER TABLE ONLY public.option_variant
    ADD CONSTRAINT option_variant_pkey PRIMARY KEY (option_variant_id);

ALTER TABLE ONLY public.option_variant
    ADD CONSTRAINT option_variant_product_option_id_option_value_id_key UNIQUE (product_option_id, option_value_id);

ALTER TABLE ONLY public.order_item
    ADD CONSTRAINT order_item_pkey PRIMARY KEY (order_item_id);

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (order_id);

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (payment_id);

ALTER TABLE ONLY public.product_option
    ADD CONSTRAINT product_option_pkey PRIMARY KEY (product_option_id);

ALTER TABLE ONLY public.product_option
    ADD CONSTRAINT product_option_sku_key UNIQUE (sku);

ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (product_id);

ALTER TABLE ONLY public.wishlist
    ADD CONSTRAINT uq_member_product UNIQUE (member_id, product_id);

ALTER TABLE ONLY public.wishlist
    ADD CONSTRAINT wishlist_pkey PRIMARY KEY (wishlist_id);

CREATE INDEX cart_item_member_id_index ON public.cart_item USING btree (member_id);

CREATE INDEX idx_cart_item_product_option_id ON public.cart_item USING btree (product_option_id);

CREATE INDEX idx_option_key_product_id ON public.option_key USING btree (product_id);

CREATE INDEX idx_option_value_key_id ON public.option_value USING btree (option_key_id);

CREATE INDEX idx_option_variant_option_id ON public.option_variant USING btree (product_option_id);

CREATE INDEX idx_option_variant_value_id ON public.option_variant USING btree (option_value_id);

CREATE INDEX idx_order_item_order_id ON public.order_item USING btree (order_id);

CREATE INDEX idx_order_item_product_option_id ON public.order_item USING btree (product_option_id);

CREATE INDEX idx_product_option_product_id ON public.product_option USING btree (product_id);

ALTER TABLE ONLY public.cart_item
    ADD CONSTRAINT cart_item_product_option_id_fkey FOREIGN KEY (product_option_id) REFERENCES public.product_option(product_option_id) ON DELETE CASCADE;

ALTER TABLE ONLY public.address
    ADD CONSTRAINT fk_address_member FOREIGN KEY (member_id) REFERENCES public.member(member_id);

ALTER TABLE ONLY public.product
    ADD CONSTRAINT fk_product_admin FOREIGN KEY (created_by) REFERENCES public.member(member_id);

ALTER TABLE ONLY public.product
    ADD CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES public.category(category_id);

ALTER TABLE ONLY public.wishlist
    ADD CONSTRAINT fk_wishlist_member FOREIGN KEY (member_id) REFERENCES public.member(member_id) ON DELETE CASCADE;

ALTER TABLE ONLY public.wishlist
    ADD CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES public.product(product_id) ON DELETE CASCADE;

ALTER TABLE ONLY public.option_key
    ADD CONSTRAINT option_key_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.product(product_id) ON DELETE CASCADE;

ALTER TABLE ONLY public.option_value
    ADD CONSTRAINT option_value_option_key_id_fkey FOREIGN KEY (option_key_id) REFERENCES public.option_key(option_key_id) ON DELETE CASCADE;

ALTER TABLE ONLY public.option_variant
    ADD CONSTRAINT option_variant_option_value_id_fkey FOREIGN KEY (option_value_id) REFERENCES public.option_value(option_value_id) ON DELETE CASCADE;

ALTER TABLE ONLY public.option_variant
    ADD CONSTRAINT option_variant_product_option_id_fkey FOREIGN KEY (product_option_id) REFERENCES public.product_option(product_option_id) ON DELETE CASCADE;

ALTER TABLE ONLY public.order_item
    ADD CONSTRAINT order_item_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.orders(order_id) ON DELETE CASCADE;

ALTER TABLE ONLY public.order_item
    ADD CONSTRAINT order_item_product_option_id_fkey FOREIGN KEY (product_option_id) REFERENCES public.product_option(product_option_id);

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_address_id_fkey FOREIGN KEY (address_id) REFERENCES public.address(address_id);

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_member_id_fkey FOREIGN KEY (member_id) REFERENCES public.member(member_id);

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_member_id_fkey FOREIGN KEY (member_id) REFERENCES public.member(member_id);

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.orders(order_id);

ALTER TABLE ONLY public.product_option
    ADD CONSTRAINT product_option_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.product(product_id) ON DELETE CASCADE;
