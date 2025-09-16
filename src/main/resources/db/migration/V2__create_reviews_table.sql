
CREATE TABLE public.review (
    review_id bigint NOT NULL,
    member_id bigint NOT NULL,
    order_item_id bigint NOT NULL,
    product_id bigint NOT NULL,
    rating integer NOT NULL,
    title varchar(255) NOT NULL,
    content text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted_at timestamp without time zone
);

CREATE SEQUENCE public.reviews_review_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.reviews_review_id_seq OWNED BY public.review.review_id;

ALTER TABLE ONLY public.review ALTER COLUMN review_id SET DEFAULT nextval('public.reviews_review_id_seq'::regclass);

ALTER TABLE ONLY public.review
    ADD CONSTRAINT reviews_pkey PRIMARY KEY (review_id);

ALTER TABLE ONLY public.review
    ADD CONSTRAINT uq_review_order_product UNIQUE (order_item_id, product_id);

ALTER TABLE ONLY public.review
    ADD CONSTRAINT fk_reviews_member FOREIGN KEY (member_id) REFERENCES public.member(member_id) ON DELETE CASCADE;

ALTER TABLE ONLY public.review
    ADD CONSTRAINT fk_reviews_order FOREIGN KEY (order_item_id) REFERENCES public.order_item(order_item_id) ON DELETE CASCADE;

ALTER TABLE ONLY public.review
    ADD CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES public.product(product_id) ON DELETE CASCADE;

CREATE INDEX idx_reviews_member_id ON public.review USING btree (member_id);
CREATE INDEX idx_reviews_product_id ON public.review USING btree (product_id);
