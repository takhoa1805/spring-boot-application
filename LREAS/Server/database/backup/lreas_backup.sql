--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0 (Debian 17.0-1.pgdg120+1)
-- Dumped by pg_dump version 17.0 (Debian 17.0-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: batch_delete_expired_rows(); Type: FUNCTION; Schema: public; Owner: lreas
--

CREATE FUNCTION public.batch_delete_expired_rows() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
        BEGIN
          EXECUTE FORMAT('WITH rows AS (SELECT ctid FROM %s WHERE %s < CURRENT_TIMESTAMP AT TIME ZONE ''UTC'' ORDER BY %s LIMIT 2 FOR UPDATE SKIP LOCKED) DELETE FROM %s WHERE ctid IN (TABLE rows)', TG_TABLE_NAME, TG_ARGV[0], TG_ARGV[0], TG_TABLE_NAME);
          RETURN NULL;
        END;
      $$;


ALTER FUNCTION public.batch_delete_expired_rows() OWNER TO lreas;

--
-- Name: sync_tags(); Type: FUNCTION; Schema: public; Owner: lreas
--

CREATE FUNCTION public.sync_tags() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
        BEGIN
          IF (TG_OP = 'TRUNCATE') THEN
            DELETE FROM tags WHERE entity_name = TG_TABLE_NAME;
            RETURN NULL;
          ELSIF (TG_OP = 'DELETE') THEN
            DELETE FROM tags WHERE entity_id = OLD.id;
            RETURN OLD;
          ELSE

          -- Triggered by INSERT/UPDATE
          -- Do an upsert on the tags table
          -- So we don't need to migrate pre 1.1 entities
          INSERT INTO tags VALUES (NEW.id, TG_TABLE_NAME, NEW.tags)
          ON CONFLICT (entity_id) DO UPDATE
                  SET tags=EXCLUDED.tags;
          END IF;
          RETURN NEW;
        END;
      $$;


ALTER FUNCTION public.sync_tags() OWNER TO lreas;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: acls; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.acls (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    consumer_id uuid,
    "group" text,
    cache_key text,
    tags text[],
    ws_id uuid
);


ALTER TABLE public.acls OWNER TO lreas;

--
-- Name: acme_storage; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.acme_storage (
    id uuid NOT NULL,
    key text,
    value text,
    created_at timestamp with time zone,
    ttl timestamp with time zone
);


ALTER TABLE public.acme_storage OWNER TO lreas;

--
-- Name: basicauth_credentials; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.basicauth_credentials (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    consumer_id uuid,
    username text,
    password text,
    tags text[],
    ws_id uuid
);


ALTER TABLE public.basicauth_credentials OWNER TO lreas;

--
-- Name: ca_certificates; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.ca_certificates (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    cert text NOT NULL,
    tags text[],
    cert_digest text NOT NULL,
    updated_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text)
);


ALTER TABLE public.ca_certificates OWNER TO lreas;

--
-- Name: certificates; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.certificates (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    cert text,
    key text,
    tags text[],
    ws_id uuid,
    cert_alt text,
    key_alt text,
    updated_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text)
);


ALTER TABLE public.certificates OWNER TO lreas;

--
-- Name: cluster_events; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.cluster_events (
    id uuid NOT NULL,
    node_id uuid NOT NULL,
    at timestamp with time zone NOT NULL,
    nbf timestamp with time zone,
    expire_at timestamp with time zone NOT NULL,
    channel text,
    data text
);


ALTER TABLE public.cluster_events OWNER TO lreas;

--
-- Name: clustering_data_planes; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.clustering_data_planes (
    id uuid NOT NULL,
    hostname text NOT NULL,
    ip text NOT NULL,
    last_seen timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    config_hash text NOT NULL,
    ttl timestamp with time zone,
    version text,
    sync_status text DEFAULT 'unknown'::text NOT NULL,
    updated_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    labels jsonb,
    cert_details jsonb,
    rpc_capabilities text[]
);


ALTER TABLE public.clustering_data_planes OWNER TO lreas;

--
-- Name: clustering_rpc_requests; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.clustering_rpc_requests (
    id bigint NOT NULL,
    node_id uuid NOT NULL,
    reply_to uuid NOT NULL,
    ttl timestamp with time zone NOT NULL,
    payload json NOT NULL
);


ALTER TABLE public.clustering_rpc_requests OWNER TO lreas;

--
-- Name: clustering_rpc_requests_id_seq; Type: SEQUENCE; Schema: public; Owner: lreas
--

CREATE SEQUENCE public.clustering_rpc_requests_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.clustering_rpc_requests_id_seq OWNER TO lreas;

--
-- Name: clustering_rpc_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lreas
--

ALTER SEQUENCE public.clustering_rpc_requests_id_seq OWNED BY public.clustering_rpc_requests.id;


--
-- Name: clustering_sync_delta; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.clustering_sync_delta (
    version integer NOT NULL,
    type text NOT NULL,
    pk json NOT NULL,
    ws_id uuid NOT NULL,
    entity json
);


ALTER TABLE public.clustering_sync_delta OWNER TO lreas;

--
-- Name: clustering_sync_version; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.clustering_sync_version (
    version integer NOT NULL
);


ALTER TABLE public.clustering_sync_version OWNER TO lreas;

--
-- Name: clustering_sync_version_version_seq; Type: SEQUENCE; Schema: public; Owner: lreas
--

CREATE SEQUENCE public.clustering_sync_version_version_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.clustering_sync_version_version_seq OWNER TO lreas;

--
-- Name: clustering_sync_version_version_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lreas
--

ALTER SEQUENCE public.clustering_sync_version_version_seq OWNED BY public.clustering_sync_version.version;


--
-- Name: consumers; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.consumers (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    username text,
    custom_id text,
    tags text[],
    ws_id uuid,
    updated_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text)
);


ALTER TABLE public.consumers OWNER TO lreas;

--
-- Name: filter_chains; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.filter_chains (
    id uuid NOT NULL,
    name text,
    enabled boolean DEFAULT true,
    route_id uuid,
    service_id uuid,
    ws_id uuid,
    cache_key text,
    filters jsonb[],
    tags text[],
    created_at timestamp with time zone,
    updated_at timestamp with time zone
);


ALTER TABLE public.filter_chains OWNER TO lreas;

--
-- Name: hmacauth_credentials; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.hmacauth_credentials (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    consumer_id uuid,
    username text,
    secret text,
    tags text[],
    ws_id uuid
);


ALTER TABLE public.hmacauth_credentials OWNER TO lreas;

--
-- Name: jwt_secrets; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.jwt_secrets (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    consumer_id uuid,
    key text,
    secret text,
    algorithm text,
    rsa_public_key text,
    tags text[],
    ws_id uuid
);


ALTER TABLE public.jwt_secrets OWNER TO lreas;

--
-- Name: key_sets; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.key_sets (
    id uuid NOT NULL,
    name text,
    tags text[],
    ws_id uuid,
    created_at timestamp with time zone,
    updated_at timestamp with time zone
);


ALTER TABLE public.key_sets OWNER TO lreas;

--
-- Name: keyauth_credentials; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.keyauth_credentials (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    consumer_id uuid,
    key text,
    tags text[],
    ttl timestamp with time zone,
    ws_id uuid
);


ALTER TABLE public.keyauth_credentials OWNER TO lreas;

--
-- Name: keys; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.keys (
    id uuid NOT NULL,
    set_id uuid,
    name text,
    cache_key text,
    ws_id uuid,
    kid text,
    jwk text,
    pem jsonb,
    tags text[],
    created_at timestamp with time zone,
    updated_at timestamp with time zone
);


ALTER TABLE public.keys OWNER TO lreas;

--
-- Name: locks; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.locks (
    key text NOT NULL,
    owner text,
    ttl timestamp with time zone
);


ALTER TABLE public.locks OWNER TO lreas;

--
-- Name: oauth2_authorization_codes; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.oauth2_authorization_codes (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    credential_id uuid,
    service_id uuid,
    code text,
    authenticated_userid text,
    scope text,
    ttl timestamp with time zone,
    challenge text,
    challenge_method text,
    ws_id uuid,
    plugin_id uuid
);


ALTER TABLE public.oauth2_authorization_codes OWNER TO lreas;

--
-- Name: oauth2_credentials; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.oauth2_credentials (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    name text,
    consumer_id uuid,
    client_id text,
    client_secret text,
    redirect_uris text[],
    tags text[],
    client_type text,
    hash_secret boolean,
    ws_id uuid
);


ALTER TABLE public.oauth2_credentials OWNER TO lreas;

--
-- Name: oauth2_tokens; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.oauth2_tokens (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    credential_id uuid,
    service_id uuid,
    access_token text,
    refresh_token text,
    token_type text,
    expires_in integer,
    authenticated_userid text,
    scope text,
    ttl timestamp with time zone,
    ws_id uuid
);


ALTER TABLE public.oauth2_tokens OWNER TO lreas;

--
-- Name: parameters; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.parameters (
    key text NOT NULL,
    value text NOT NULL,
    created_at timestamp with time zone
);


ALTER TABLE public.parameters OWNER TO lreas;

--
-- Name: plugins; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.plugins (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    name text NOT NULL,
    consumer_id uuid,
    service_id uuid,
    route_id uuid,
    config jsonb NOT NULL,
    enabled boolean NOT NULL,
    cache_key text,
    protocols text[],
    tags text[],
    ws_id uuid,
    instance_name text,
    updated_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text)
);


ALTER TABLE public.plugins OWNER TO lreas;

--
-- Name: ratelimiting_metrics; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.ratelimiting_metrics (
    identifier text NOT NULL,
    period text NOT NULL,
    period_date timestamp with time zone NOT NULL,
    service_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid NOT NULL,
    route_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid NOT NULL,
    value integer,
    ttl timestamp with time zone
);


ALTER TABLE public.ratelimiting_metrics OWNER TO lreas;

--
-- Name: response_ratelimiting_metrics; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.response_ratelimiting_metrics (
    identifier text NOT NULL,
    period text NOT NULL,
    period_date timestamp with time zone NOT NULL,
    service_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid NOT NULL,
    route_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid NOT NULL,
    value integer
);


ALTER TABLE public.response_ratelimiting_metrics OWNER TO lreas;

--
-- Name: routes; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.routes (
    id uuid NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    name text,
    service_id uuid,
    protocols text[],
    methods text[],
    hosts text[],
    paths text[],
    snis text[],
    sources jsonb[],
    destinations jsonb[],
    regex_priority bigint,
    strip_path boolean,
    preserve_host boolean,
    tags text[],
    https_redirect_status_code integer,
    headers jsonb,
    path_handling text DEFAULT 'v0'::text,
    ws_id uuid,
    request_buffering boolean,
    response_buffering boolean,
    expression text,
    priority bigint
);


ALTER TABLE public.routes OWNER TO lreas;

--
-- Name: schema_meta; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.schema_meta (
    key text NOT NULL,
    subsystem text NOT NULL,
    last_executed text,
    executed text[],
    pending text[]
);


ALTER TABLE public.schema_meta OWNER TO lreas;

--
-- Name: services; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.services (
    id uuid NOT NULL,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    name text,
    retries bigint,
    protocol text,
    host text,
    port bigint,
    path text,
    connect_timeout bigint,
    write_timeout bigint,
    read_timeout bigint,
    tags text[],
    client_certificate_id uuid,
    tls_verify boolean,
    tls_verify_depth smallint,
    ca_certificates uuid[],
    ws_id uuid,
    enabled boolean DEFAULT true
);


ALTER TABLE public.services OWNER TO lreas;

--
-- Name: sessions; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.sessions (
    id uuid NOT NULL,
    session_id text,
    expires integer,
    data text,
    created_at timestamp with time zone,
    ttl timestamp with time zone
);


ALTER TABLE public.sessions OWNER TO lreas;

--
-- Name: sm_vaults; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.sm_vaults (
    id uuid NOT NULL,
    ws_id uuid,
    prefix text,
    name text NOT NULL,
    description text,
    config jsonb NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    updated_at timestamp with time zone,
    tags text[]
);


ALTER TABLE public.sm_vaults OWNER TO lreas;

--
-- Name: snis; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.snis (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    name text NOT NULL,
    certificate_id uuid,
    tags text[],
    ws_id uuid,
    updated_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text)
);


ALTER TABLE public.snis OWNER TO lreas;

--
-- Name: tags; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.tags (
    entity_id uuid NOT NULL,
    entity_name text,
    tags text[]
);


ALTER TABLE public.tags OWNER TO lreas;

--
-- Name: targets; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.targets (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(3) AT TIME ZONE 'UTC'::text),
    upstream_id uuid,
    target text NOT NULL,
    weight integer NOT NULL,
    tags text[],
    ws_id uuid,
    cache_key text,
    updated_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(3) AT TIME ZONE 'UTC'::text)
);


ALTER TABLE public.targets OWNER TO lreas;

--
-- Name: upstreams; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.upstreams (
    id uuid NOT NULL,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(3) AT TIME ZONE 'UTC'::text),
    name text,
    hash_on text,
    hash_fallback text,
    hash_on_header text,
    hash_fallback_header text,
    hash_on_cookie text,
    hash_on_cookie_path text,
    slots integer NOT NULL,
    healthchecks jsonb,
    tags text[],
    algorithm text,
    host_header text,
    client_certificate_id uuid,
    ws_id uuid,
    hash_on_query_arg text,
    hash_fallback_query_arg text,
    hash_on_uri_capture text,
    hash_fallback_uri_capture text,
    use_srv_name boolean DEFAULT false,
    updated_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text)
);


ALTER TABLE public.upstreams OWNER TO lreas;

--
-- Name: workspaces; Type: TABLE; Schema: public; Owner: lreas
--

CREATE TABLE public.workspaces (
    id uuid NOT NULL,
    name text,
    comment text,
    created_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text),
    meta jsonb,
    config jsonb,
    updated_at timestamp with time zone DEFAULT (CURRENT_TIMESTAMP(0) AT TIME ZONE 'UTC'::text)
);


ALTER TABLE public.workspaces OWNER TO lreas;

--
-- Name: clustering_rpc_requests id; Type: DEFAULT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.clustering_rpc_requests ALTER COLUMN id SET DEFAULT nextval('public.clustering_rpc_requests_id_seq'::regclass);


--
-- Name: clustering_sync_version version; Type: DEFAULT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.clustering_sync_version ALTER COLUMN version SET DEFAULT nextval('public.clustering_sync_version_version_seq'::regclass);


--
-- Data for Name: acls; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.acls (id, created_at, consumer_id, "group", cache_key, tags, ws_id) FROM stdin;
28a739e3-f26a-4975-9b5a-7ab9be73917b	2025-02-12 11:33:31+00	29eaec0c-273b-40e8-8b05-e7c4b92e0bcc	superuser	acls:29eaec0c-273b-40e8-8b05-e7c4b92e0bcc:superuser::::94311e55-a7de-4969-a119-d342ffd11293	\N	94311e55-a7de-4969-a119-d342ffd11293
a2b00a17-aa12-404c-af99-04b714882ee1	2025-02-12 11:33:44+00	481f9538-1cc9-4f14-90d1-d0d61a05067f	teacher	acls:481f9538-1cc9-4f14-90d1-d0d61a05067f:teacher::::94311e55-a7de-4969-a119-d342ffd11293	\N	94311e55-a7de-4969-a119-d342ffd11293
fa6320ba-7728-45f0-b094-4bbbca952bdd	2025-02-12 11:34:02+00	7ffa6417-f845-4dfc-bee1-2e6f84d6a785	admin	acls:7ffa6417-f845-4dfc-bee1-2e6f84d6a785:admin::::94311e55-a7de-4969-a119-d342ffd11293	\N	94311e55-a7de-4969-a119-d342ffd11293
41af63bd-1033-484f-b53f-88298f8c254d	2025-02-12 11:34:12+00	f50e70b6-33cd-4307-a0c1-612d1767d9e9	student	acls:f50e70b6-33cd-4307-a0c1-612d1767d9e9:student::::94311e55-a7de-4969-a119-d342ffd11293	\N	94311e55-a7de-4969-a119-d342ffd11293
\.


--
-- Data for Name: acme_storage; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.acme_storage (id, key, value, created_at, ttl) FROM stdin;
\.


--
-- Data for Name: basicauth_credentials; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.basicauth_credentials (id, created_at, consumer_id, username, password, tags, ws_id) FROM stdin;
\.


--
-- Data for Name: ca_certificates; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.ca_certificates (id, created_at, cert, tags, cert_digest, updated_at) FROM stdin;
\.


--
-- Data for Name: certificates; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.certificates (id, created_at, cert, key, tags, ws_id, cert_alt, key_alt, updated_at) FROM stdin;
\.


--
-- Data for Name: cluster_events; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.cluster_events (id, node_id, at, nbf, expire_at, channel, data) FROM stdin;
5736d459-a150-4c6d-ab6d-bdf64219179a	6c24cde3-1756-4d96-bac0-e6dfe2a3103b	2025-05-20 20:07:31.495+00	\N	2025-05-20 21:07:31.495+00	invalidations_kong_core_db_cache	services:c912f032-9292-40a8-9d5a-1887e19a4fae:::::94311e55-a7de-4969-a119-d342ffd11293
05bdf7e0-1941-4566-b042-00b16764b0b3	6c24cde3-1756-4d96-bac0-e6dfe2a3103b	2025-05-20 20:07:48.647+00	\N	2025-05-20 21:07:48.647+00	invalidations_kong_core_db_cache	routes:67a7e9ae-0644-466c-9337-3effa54ad4d6:::::94311e55-a7de-4969-a119-d342ffd11293
9ac47ed3-7111-44d0-83e2-53fb6550287e	6c24cde3-1756-4d96-bac0-e6dfe2a3103b	2025-05-20 20:07:48.65+00	\N	2025-05-20 21:07:48.65+00	invalidations_kong_core_db_cache	router:version
066e071e-2629-43f4-9e82-0cbc4f14b77e	6c24cde3-1756-4d96-bac0-e6dfe2a3103b	2025-05-20 20:07:58.791+00	\N	2025-05-20 21:07:58.791+00	invalidations_kong_core_db_cache	services:c912f032-9292-40a8-9d5a-1887e19a4fae:::::94311e55-a7de-4969-a119-d342ffd11293
4f2c7be2-d556-4d02-8f63-4b4025ea8351	6c24cde3-1756-4d96-bac0-e6dfe2a3103b	2025-05-20 20:07:58.794+00	\N	2025-05-20 21:07:58.794+00	invalidations_kong_core_db_cache	router:version
7ff7af31-aac3-428e-b9bc-e781bb39335a	6c24cde3-1756-4d96-bac0-e6dfe2a3103b	2025-05-20 20:08:30.713+00	\N	2025-05-20 21:08:30.713+00	invalidations_kong_core_db_cache	routes:67a7e9ae-0644-466c-9337-3effa54ad4d6:::::94311e55-a7de-4969-a119-d342ffd11293
82d65fdd-a702-4c7e-a140-03b30551b679	6c24cde3-1756-4d96-bac0-e6dfe2a3103b	2025-05-20 20:08:30.716+00	\N	2025-05-20 21:08:30.716+00	invalidations_kong_core_db_cache	router:version
\.


--
-- Data for Name: clustering_data_planes; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.clustering_data_planes (id, hostname, ip, last_seen, config_hash, ttl, version, sync_status, updated_at, labels, cert_details, rpc_capabilities) FROM stdin;
\.


--
-- Data for Name: clustering_rpc_requests; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.clustering_rpc_requests (id, node_id, reply_to, ttl, payload) FROM stdin;
\.


--
-- Data for Name: clustering_sync_delta; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.clustering_sync_delta (version, type, pk, ws_id, entity) FROM stdin;
\.


--
-- Data for Name: clustering_sync_version; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.clustering_sync_version (version) FROM stdin;
\.


--
-- Data for Name: consumers; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.consumers (id, created_at, username, custom_id, tags, ws_id, updated_at) FROM stdin;
29eaec0c-273b-40e8-8b05-e7c4b92e0bcc	2025-02-12 11:16:57+00	khoa	test	{}	94311e55-a7de-4969-a119-d342ffd11293	2025-02-24 08:49:10+00
481f9538-1cc9-4f14-90d1-d0d61a05067f	2025-02-12 11:24:52+00	teacher	teacher	{}	94311e55-a7de-4969-a119-d342ffd11293	2025-02-24 08:49:21+00
7ffa6417-f845-4dfc-bee1-2e6f84d6a785	2025-02-12 11:24:28+00	admin	admin	{}	94311e55-a7de-4969-a119-d342ffd11293	2025-02-24 08:49:28+00
f50e70b6-33cd-4307-a0c1-612d1767d9e9	2025-02-12 11:25:09+00	student	student	{}	94311e55-a7de-4969-a119-d342ffd11293	2025-02-24 08:49:37+00
\.


--
-- Data for Name: filter_chains; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.filter_chains (id, name, enabled, route_id, service_id, ws_id, cache_key, filters, tags, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: hmacauth_credentials; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.hmacauth_credentials (id, created_at, consumer_id, username, secret, tags, ws_id) FROM stdin;
\.


--
-- Data for Name: jwt_secrets; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.jwt_secrets (id, created_at, consumer_id, key, secret, algorithm, rsa_public_key, tags, ws_id) FROM stdin;
4120e7e6-d8f0-44c6-bff6-aae0bf967fd8	2025-02-12 11:18:19+00	29eaec0c-273b-40e8-8b05-e7c4b92e0bcc	KmrtiN1X53Nefp0xkiVzRTUsEg3Bzo5F	zo4sIfBAIyOSVlD7okXbzUHnsGulnl7U	HS256	\N	\N	94311e55-a7de-4969-a119-d342ffd11293
d1c84b7c-8a45-4d0e-b83a-eed2f7dc6a3e	2025-02-12 11:24:40+00	7ffa6417-f845-4dfc-bee1-2e6f84d6a785	p96lO4ewSofzfYTvOJc1kKhP4Kfb32aJ	1ILGuygCmEM44lSsuUBJT7yAjwOC0vIV	HS256	\N	\N	94311e55-a7de-4969-a119-d342ffd11293
a667d140-0302-4c7e-ba75-dc769ab5d125	2025-02-12 11:24:57+00	481f9538-1cc9-4f14-90d1-d0d61a05067f	XKAyMEJZmef9vXs57ofKXgDpW7LX9KP0	wwS6XWMSQYpUacDg5abZT3SMYWEjPFjM	HS256	\N	\N	94311e55-a7de-4969-a119-d342ffd11293
65095365-2ef0-41d8-81e9-f12bb4933f73	2025-02-12 11:25:25+00	f50e70b6-33cd-4307-a0c1-612d1767d9e9	7BA75TMmxOrlzPMcIxW6bOxRvus1iAJX	wNR9IRIyqJq04XbaMLcBtO3lcsBoeLHJ	HS256	\N	\N	94311e55-a7de-4969-a119-d342ffd11293
\.


--
-- Data for Name: key_sets; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.key_sets (id, name, tags, ws_id, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: keyauth_credentials; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.keyauth_credentials (id, created_at, consumer_id, key, tags, ttl, ws_id) FROM stdin;
\.


--
-- Data for Name: keys; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.keys (id, set_id, name, cache_key, ws_id, kid, jwk, pem, tags, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: locks; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.locks (key, owner, ttl) FROM stdin;
\.


--
-- Data for Name: oauth2_authorization_codes; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.oauth2_authorization_codes (id, created_at, credential_id, service_id, code, authenticated_userid, scope, ttl, challenge, challenge_method, ws_id, plugin_id) FROM stdin;
\.


--
-- Data for Name: oauth2_credentials; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.oauth2_credentials (id, created_at, name, consumer_id, client_id, client_secret, redirect_uris, tags, client_type, hash_secret, ws_id) FROM stdin;
\.


--
-- Data for Name: oauth2_tokens; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.oauth2_tokens (id, created_at, credential_id, service_id, access_token, refresh_token, token_type, expires_in, authenticated_userid, scope, ttl, ws_id) FROM stdin;
\.


--
-- Data for Name: parameters; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.parameters (key, value, created_at) FROM stdin;
cluster_id	5effd2c4-d96b-4036-a7e5-b7374810df28	\N
\.


--
-- Data for Name: plugins; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.plugins (id, created_at, name, consumer_id, service_id, route_id, config, enabled, cache_key, protocols, tags, ws_id, instance_name, updated_at) FROM stdin;
30022dcb-e519-41a3-afda-556af7300c8b	2025-02-12 11:13:05+00	jwt	\N	\N	\N	{"realm": null, "anonymous": null, "cookie_names": [], "header_names": ["authorization"], "key_claim_name": "iss", "uri_param_names": ["jwt"], "claims_to_verify": null, "run_on_preflight": true, "secret_is_base64": false, "maximum_expiration": 0}	f	plugins:jwt:::::94311e55-a7de-4969-a119-d342ffd11293	{grpc,grpcs,http,https}	\N	94311e55-a7de-4969-a119-d342ffd11293	\N	2025-02-12 11:15:44+00
cee4ff07-ac51-44c1-8cbe-252d672a8b59	2025-02-12 11:28:51+00	acl	\N	\N	ee0d2a61-db80-4c27-9d4a-3c31d1109d23	{"deny": null, "allow": ["public"], "hide_groups_header": false, "always_use_authenticated_groups": false}	f	plugins:acl:ee0d2a61-db80-4c27-9d4a-3c31d1109d23::::94311e55-a7de-4969-a119-d342ffd11293	{grpc,grpcs,http,https}	\N	94311e55-a7de-4969-a119-d342ffd11293	\N	2025-02-12 11:29:08+00
55238389-6d81-444c-81a2-a077e179d455	2025-03-04 15:58:51+00	cors	\N	\N	\N	{"headers": null, "max_age": null, "methods": ["GET", "HEAD", "PUT", "PATCH", "POST", "DELETE", "OPTIONS", "TRACE", "CONNECT"], "origins": null, "credentials": false, "exposed_headers": null, "private_network": false, "preflight_continue": false}	t	plugins:cors:::::94311e55-a7de-4969-a119-d342ffd11293	{grpc,grpcs,http,https}	\N	94311e55-a7de-4969-a119-d342ffd11293	\N	2025-03-04 15:58:51+00
\.


--
-- Data for Name: ratelimiting_metrics; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.ratelimiting_metrics (identifier, period, period_date, service_id, route_id, value, ttl) FROM stdin;
\.


--
-- Data for Name: response_ratelimiting_metrics; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.response_ratelimiting_metrics (identifier, period, period_date, service_id, route_id, value) FROM stdin;
\.


--
-- Data for Name: routes; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.routes (id, created_at, updated_at, name, service_id, protocols, methods, hosts, paths, snis, sources, destinations, regex_priority, strip_path, preserve_host, tags, https_redirect_status_code, headers, path_handling, ws_id, request_buffering, response_buffering, expression, priority) FROM stdin;
ee0d2a61-db80-4c27-9d4a-3c31d1109d23	2025-02-12 08:45:57+00	2025-02-12 08:45:57+00	web_app	8cf3eb73-e717-4bb1-8e3d-ec5c291c9854	{http,https}	\N	\N	{/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
bcc5beb9-1f28-4e3e-80dd-2d0cb28ad689	2025-02-18 16:51:58+00	2025-02-18 16:51:58+00	authentication	75e3f5cd-f6d7-41d4-afd6-110779a2673c	{http,https}	\N	\N	{/api/authentication/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
a2308e0c-3e6d-48b0-821f-d836c71d167b	2025-02-28 05:57:00+00	2025-02-28 05:57:00+00	file-management	ddef5eca-cdf1-42fc-b54c-c9a41923f739	{http,https}	\N	\N	{/api/file-management/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
7ef5383a-de97-4acd-b21c-9ed5a8b94997	2025-03-01 15:50:42+00	2025-03-03 14:26:37+00	generator	1dadb1cd-4ec3-4b18-b4d0-d3521e13a7f4	{http,https}	\N	\N	{/api/generator/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
f39a7454-c288-4c55-bad3-6ff00908f759	2025-03-03 14:26:09+00	2025-03-03 14:26:52+00	quiz	5a2c7f77-ae08-4576-8595-f7a3e04d3ee1	{http,https}	\N	\N	{/api/quiz/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
31b53b44-343a-4697-8bdf-794745288543	2025-03-28 12:48:49+00	2025-03-28 12:48:49+00	profile	3518bc3c-984d-422f-bafb-480d93bd8afe	{http,https}	\N	\N	{/api/profile/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
a8b21b1f-bda7-49bb-b4fc-7b6b81e2caf6	2025-04-02 07:17:18+00	2025-04-02 07:17:18+00	forum	b86d6df7-efbd-4486-8746-2ce0e83c814b	{http,https}	\N	\N	{/api/forum/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
2650d1c5-9bc8-4418-82d3-cd1de1c56b3c	2025-04-14 12:48:36+00	2025-04-14 12:48:36+00	documents	56233cf4-f85a-4861-9354-01a96e411a2f	{http,https}	\N	\N	{/api/documents/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
220ce5c2-41f7-4598-94f7-03601d967e76	2025-04-16 01:56:22+00	2025-04-16 01:56:22+00	real-time-quiz	4ca918b0-22ec-431a-b29a-ca4a3c544a6a	{http,https}	\N	\N	{/api/competitive-quiz}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
44475074-d961-425d-92e7-63c150c184fc	2025-04-16 14:27:23+00	2025-04-16 14:27:23+00	admin	e2983805-e6b9-4c95-8322-cab3f04c98ed	{http,https}	\N	\N	{/api/admin/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
be807460-fc60-4250-b6d0-6c743124943e	2025-05-20 15:57:29+00	2025-05-20 15:57:38+00	minio	e0857f1c-b650-48d5-8580-e46e35d555dc	{http,https}	\N	\N	{/api/minio/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
67a7e9ae-0644-466c-9337-3effa54ad4d6	2025-05-20 20:07:48+00	2025-05-20 20:08:30+00	email	c912f032-9292-40a8-9d5a-1887e19a4fae	{http,https}	\N	\N	{/api/email/}	\N	\N	\N	0	t	f	{}	426	\N	v0	94311e55-a7de-4969-a119-d342ffd11293	t	t	\N	\N
\.


--
-- Data for Name: schema_meta; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.schema_meta (key, subsystem, last_executed, executed, pending) FROM stdin;
schema_meta	core	024_380_to_390	{000_base,003_100_to_110,004_110_to_120,005_120_to_130,006_130_to_140,007_140_to_150,008_150_to_200,009_200_to_210,010_210_to_211,011_212_to_213,012_213_to_220,013_220_to_230,014_230_to_270,015_270_to_280,016_280_to_300,017_300_to_310,018_310_to_320,019_320_to_330,020_330_to_340,021_340_to_350,022_350_to_360,023_360_to_370,024_380_to_390}	{}
schema_meta	oauth2	007_320_to_330	{000_base_oauth2,003_130_to_140,004_200_to_210,005_210_to_211,006_320_to_330,007_320_to_330}	{}
schema_meta	acl	004_212_to_213	{000_base_acl,002_130_to_140,003_200_to_210,004_212_to_213}	{}
schema_meta	opentelemetry	001_331_to_332	{001_331_to_332}	{}
schema_meta	post-function	001_280_to_300	{001_280_to_300}	{}
schema_meta	acme	003_350_to_360	{000_base_acme,001_280_to_300,002_320_to_330,003_350_to_360}	{}
schema_meta	ai-proxy	001_360_to_370	{001_360_to_370}	{}
schema_meta	pre-function	001_280_to_300	{001_280_to_300}	{}
schema_meta	basic-auth	003_200_to_210	{000_base_basic_auth,002_130_to_140,003_200_to_210}	{}
schema_meta	bot-detection	001_200_to_210	{001_200_to_210}	{}
schema_meta	hmac-auth	003_200_to_210	{000_base_hmac_auth,002_130_to_140,003_200_to_210}	{}
schema_meta	http-log	001_280_to_300	{001_280_to_300}	{}
schema_meta	ip-restriction	001_200_to_210	{001_200_to_210}	{}
schema_meta	jwt	003_200_to_210	{000_base_jwt,002_130_to_140,003_200_to_210}	{}
schema_meta	rate-limiting	006_350_to_360	{000_base_rate_limiting,003_10_to_112,004_200_to_210,005_320_to_330,006_350_to_360}	{}
schema_meta	key-auth	004_320_to_330	{000_base_key_auth,002_130_to_140,003_200_to_210,004_320_to_330}	{}
schema_meta	response-ratelimiting	001_350_to_360	{000_base_response_rate_limiting,001_350_to_360}	{}
schema_meta	session	002_320_to_330	{000_base_session,001_add_ttl_index,002_320_to_330}	\N
\.


--
-- Data for Name: services; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.services (id, created_at, updated_at, name, retries, protocol, host, port, path, connect_timeout, write_timeout, read_timeout, tags, client_certificate_id, tls_verify, tls_verify_depth, ca_certificates, ws_id, enabled) FROM stdin;
8cf3eb73-e717-4bb1-8e3d-ec5c291c9854	2025-02-12 08:45:40+00	2025-02-12 08:45:40+00	web_app	5	http	client	3000	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
75e3f5cd-f6d7-41d4-afd6-110779a2673c	2025-02-18 16:51:43+00	2025-02-18 16:51:43+00	authentication	5	http	authentication-service	2001	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
ddef5eca-cdf1-42fc-b54c-c9a41923f739	2025-02-28 05:56:38+00	2025-02-28 05:56:38+00	file-management	5	http	file-management-service	2005	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
1dadb1cd-4ec3-4b18-b4d0-d3521e13a7f4	2025-03-01 15:50:26+00	2025-03-01 15:50:26+00	generator	5	http	generator-service	2004	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
5a2c7f77-ae08-4576-8595-f7a3e04d3ee1	2025-03-03 14:25:51+00	2025-03-03 14:25:51+00	quiz	5	http	quiz-service	2003	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
3518bc3c-984d-422f-bafb-480d93bd8afe	2025-03-28 12:48:27+00	2025-03-28 12:48:27+00	profile	5	http	profile-service	2002	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
b86d6df7-efbd-4486-8746-2ce0e83c814b	2025-04-02 07:16:49+00	2025-04-02 07:16:49+00	forum	5	http	forum-service	2006	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
56233cf4-f85a-4861-9354-01a96e411a2f	2025-04-14 12:48:11+00	2025-04-14 12:50:19+00	documents	5	http	documents	5000	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
4ca918b0-22ec-431a-b29a-ca4a3c544a6a	2025-04-16 01:56:00+00	2025-04-16 01:56:00+00	real_time_quiz	5	http	real-time-quiz	5001	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
e2983805-e6b9-4c95-8322-cab3f04c98ed	2025-04-16 14:26:46+00	2025-04-16 14:26:46+00	admin	5	http	admin-service	2007	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
e0857f1c-b650-48d5-8580-e46e35d555dc	2025-05-20 15:56:55+00	2025-05-20 15:57:05+00	minio	5	http	minio	9000	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
c912f032-9292-40a8-9d5a-1887e19a4fae	2025-05-20 20:07:31+00	2025-05-20 20:07:58+00	email	5	http	email-service	2010	\N	60000	60000	60000	\N	\N	\N	\N	\N	94311e55-a7de-4969-a119-d342ffd11293	t
\.


--
-- Data for Name: sessions; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.sessions (id, session_id, expires, data, created_at, ttl) FROM stdin;
\.


--
-- Data for Name: sm_vaults; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.sm_vaults (id, ws_id, prefix, name, description, config, created_at, updated_at, tags) FROM stdin;
\.


--
-- Data for Name: snis; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.snis (id, created_at, name, certificate_id, tags, ws_id, updated_at) FROM stdin;
\.


--
-- Data for Name: tags; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.tags (entity_id, entity_name, tags) FROM stdin;
8cf3eb73-e717-4bb1-8e3d-ec5c291c9854	services	\N
ee0d2a61-db80-4c27-9d4a-3c31d1109d23	routes	{}
30022dcb-e519-41a3-afda-556af7300c8b	plugins	\N
4120e7e6-d8f0-44c6-bff6-aae0bf967fd8	jwt_secrets	\N
d1c84b7c-8a45-4d0e-b83a-eed2f7dc6a3e	jwt_secrets	\N
a667d140-0302-4c7e-ba75-dc769ab5d125	jwt_secrets	\N
65095365-2ef0-41d8-81e9-f12bb4933f73	jwt_secrets	\N
cee4ff07-ac51-44c1-8cbe-252d672a8b59	plugins	\N
28a739e3-f26a-4975-9b5a-7ab9be73917b	acls	\N
a2b00a17-aa12-404c-af99-04b714882ee1	acls	\N
fa6320ba-7728-45f0-b094-4bbbca952bdd	acls	\N
41af63bd-1033-484f-b53f-88298f8c254d	acls	\N
75e3f5cd-f6d7-41d4-afd6-110779a2673c	services	\N
bcc5beb9-1f28-4e3e-80dd-2d0cb28ad689	routes	{}
29eaec0c-273b-40e8-8b05-e7c4b92e0bcc	consumers	{}
481f9538-1cc9-4f14-90d1-d0d61a05067f	consumers	{}
7ffa6417-f845-4dfc-bee1-2e6f84d6a785	consumers	{}
f50e70b6-33cd-4307-a0c1-612d1767d9e9	consumers	{}
ddef5eca-cdf1-42fc-b54c-c9a41923f739	services	\N
a2308e0c-3e6d-48b0-821f-d836c71d167b	routes	{}
1dadb1cd-4ec3-4b18-b4d0-d3521e13a7f4	services	\N
5a2c7f77-ae08-4576-8595-f7a3e04d3ee1	services	\N
7ef5383a-de97-4acd-b21c-9ed5a8b94997	routes	{}
f39a7454-c288-4c55-bad3-6ff00908f759	routes	{}
55238389-6d81-444c-81a2-a077e179d455	plugins	\N
3518bc3c-984d-422f-bafb-480d93bd8afe	services	\N
31b53b44-343a-4697-8bdf-794745288543	routes	{}
b86d6df7-efbd-4486-8746-2ce0e83c814b	services	\N
a8b21b1f-bda7-49bb-b4fc-7b6b81e2caf6	routes	{}
2650d1c5-9bc8-4418-82d3-cd1de1c56b3c	routes	{}
56233cf4-f85a-4861-9354-01a96e411a2f	services	\N
4ca918b0-22ec-431a-b29a-ca4a3c544a6a	services	\N
220ce5c2-41f7-4598-94f7-03601d967e76	routes	{}
e2983805-e6b9-4c95-8322-cab3f04c98ed	services	\N
44475074-d961-425d-92e7-63c150c184fc	routes	{}
e0857f1c-b650-48d5-8580-e46e35d555dc	services	\N
be807460-fc60-4250-b6d0-6c743124943e	routes	{}
c912f032-9292-40a8-9d5a-1887e19a4fae	services	\N
67a7e9ae-0644-466c-9337-3effa54ad4d6	routes	{}
\.


--
-- Data for Name: targets; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.targets (id, created_at, upstream_id, target, weight, tags, ws_id, cache_key, updated_at) FROM stdin;
\.


--
-- Data for Name: upstreams; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.upstreams (id, created_at, name, hash_on, hash_fallback, hash_on_header, hash_fallback_header, hash_on_cookie, hash_on_cookie_path, slots, healthchecks, tags, algorithm, host_header, client_certificate_id, ws_id, hash_on_query_arg, hash_fallback_query_arg, hash_on_uri_capture, hash_fallback_uri_capture, use_srv_name, updated_at) FROM stdin;
\.


--
-- Data for Name: workspaces; Type: TABLE DATA; Schema: public; Owner: lreas
--

COPY public.workspaces (id, name, comment, created_at, meta, config, updated_at) FROM stdin;
94311e55-a7de-4969-a119-d342ffd11293	default	\N	2025-02-12 08:34:54+00	\N	\N	2025-02-12 08:34:54+00
\.


--
-- Name: clustering_rpc_requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: lreas
--

SELECT pg_catalog.setval('public.clustering_rpc_requests_id_seq', 1, false);


--
-- Name: clustering_sync_version_version_seq; Type: SEQUENCE SET; Schema: public; Owner: lreas
--

SELECT pg_catalog.setval('public.clustering_sync_version_version_seq', 1, false);


--
-- Name: acls acls_cache_key_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.acls
    ADD CONSTRAINT acls_cache_key_key UNIQUE (cache_key);


--
-- Name: acls acls_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.acls
    ADD CONSTRAINT acls_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: acls acls_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.acls
    ADD CONSTRAINT acls_pkey PRIMARY KEY (id);


--
-- Name: acme_storage acme_storage_key_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.acme_storage
    ADD CONSTRAINT acme_storage_key_key UNIQUE (key);


--
-- Name: acme_storage acme_storage_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.acme_storage
    ADD CONSTRAINT acme_storage_pkey PRIMARY KEY (id);


--
-- Name: basicauth_credentials basicauth_credentials_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.basicauth_credentials
    ADD CONSTRAINT basicauth_credentials_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: basicauth_credentials basicauth_credentials_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.basicauth_credentials
    ADD CONSTRAINT basicauth_credentials_pkey PRIMARY KEY (id);


--
-- Name: basicauth_credentials basicauth_credentials_ws_id_username_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.basicauth_credentials
    ADD CONSTRAINT basicauth_credentials_ws_id_username_unique UNIQUE (ws_id, username);


--
-- Name: ca_certificates ca_certificates_cert_digest_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.ca_certificates
    ADD CONSTRAINT ca_certificates_cert_digest_key UNIQUE (cert_digest);


--
-- Name: ca_certificates ca_certificates_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.ca_certificates
    ADD CONSTRAINT ca_certificates_pkey PRIMARY KEY (id);


--
-- Name: certificates certificates_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.certificates
    ADD CONSTRAINT certificates_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: certificates certificates_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.certificates
    ADD CONSTRAINT certificates_pkey PRIMARY KEY (id);


--
-- Name: cluster_events cluster_events_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.cluster_events
    ADD CONSTRAINT cluster_events_pkey PRIMARY KEY (id);


--
-- Name: clustering_data_planes clustering_data_planes_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.clustering_data_planes
    ADD CONSTRAINT clustering_data_planes_pkey PRIMARY KEY (id);


--
-- Name: clustering_rpc_requests clustering_rpc_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.clustering_rpc_requests
    ADD CONSTRAINT clustering_rpc_requests_pkey PRIMARY KEY (id);


--
-- Name: clustering_sync_version clustering_sync_version_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.clustering_sync_version
    ADD CONSTRAINT clustering_sync_version_pkey PRIMARY KEY (version);


--
-- Name: consumers consumers_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.consumers
    ADD CONSTRAINT consumers_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: consumers consumers_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.consumers
    ADD CONSTRAINT consumers_pkey PRIMARY KEY (id);


--
-- Name: consumers consumers_ws_id_custom_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.consumers
    ADD CONSTRAINT consumers_ws_id_custom_id_unique UNIQUE (ws_id, custom_id);


--
-- Name: consumers consumers_ws_id_username_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.consumers
    ADD CONSTRAINT consumers_ws_id_username_unique UNIQUE (ws_id, username);


--
-- Name: filter_chains filter_chains_cache_key_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.filter_chains
    ADD CONSTRAINT filter_chains_cache_key_key UNIQUE (cache_key);


--
-- Name: filter_chains filter_chains_name_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.filter_chains
    ADD CONSTRAINT filter_chains_name_key UNIQUE (name);


--
-- Name: filter_chains filter_chains_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.filter_chains
    ADD CONSTRAINT filter_chains_pkey PRIMARY KEY (id);


--
-- Name: hmacauth_credentials hmacauth_credentials_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.hmacauth_credentials
    ADD CONSTRAINT hmacauth_credentials_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: hmacauth_credentials hmacauth_credentials_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.hmacauth_credentials
    ADD CONSTRAINT hmacauth_credentials_pkey PRIMARY KEY (id);


--
-- Name: hmacauth_credentials hmacauth_credentials_ws_id_username_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.hmacauth_credentials
    ADD CONSTRAINT hmacauth_credentials_ws_id_username_unique UNIQUE (ws_id, username);


--
-- Name: jwt_secrets jwt_secrets_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.jwt_secrets
    ADD CONSTRAINT jwt_secrets_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: jwt_secrets jwt_secrets_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.jwt_secrets
    ADD CONSTRAINT jwt_secrets_pkey PRIMARY KEY (id);


--
-- Name: jwt_secrets jwt_secrets_ws_id_key_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.jwt_secrets
    ADD CONSTRAINT jwt_secrets_ws_id_key_unique UNIQUE (ws_id, key);


--
-- Name: key_sets key_sets_name_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.key_sets
    ADD CONSTRAINT key_sets_name_key UNIQUE (name);


--
-- Name: key_sets key_sets_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.key_sets
    ADD CONSTRAINT key_sets_pkey PRIMARY KEY (id);


--
-- Name: keyauth_credentials keyauth_credentials_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keyauth_credentials
    ADD CONSTRAINT keyauth_credentials_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: keyauth_credentials keyauth_credentials_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keyauth_credentials
    ADD CONSTRAINT keyauth_credentials_pkey PRIMARY KEY (id);


--
-- Name: keyauth_credentials keyauth_credentials_ws_id_key_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keyauth_credentials
    ADD CONSTRAINT keyauth_credentials_ws_id_key_unique UNIQUE (ws_id, key);


--
-- Name: keys keys_cache_key_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keys
    ADD CONSTRAINT keys_cache_key_key UNIQUE (cache_key);


--
-- Name: keys keys_kid_set_id_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keys
    ADD CONSTRAINT keys_kid_set_id_key UNIQUE (kid, set_id);


--
-- Name: keys keys_name_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keys
    ADD CONSTRAINT keys_name_key UNIQUE (name);


--
-- Name: keys keys_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keys
    ADD CONSTRAINT keys_pkey PRIMARY KEY (id);


--
-- Name: locks locks_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.locks
    ADD CONSTRAINT locks_pkey PRIMARY KEY (key);


--
-- Name: oauth2_authorization_codes oauth2_authorization_codes_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_authorization_codes
    ADD CONSTRAINT oauth2_authorization_codes_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: oauth2_authorization_codes oauth2_authorization_codes_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_authorization_codes
    ADD CONSTRAINT oauth2_authorization_codes_pkey PRIMARY KEY (id);


--
-- Name: oauth2_authorization_codes oauth2_authorization_codes_ws_id_code_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_authorization_codes
    ADD CONSTRAINT oauth2_authorization_codes_ws_id_code_unique UNIQUE (ws_id, code);


--
-- Name: oauth2_credentials oauth2_credentials_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_credentials
    ADD CONSTRAINT oauth2_credentials_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: oauth2_credentials oauth2_credentials_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_credentials
    ADD CONSTRAINT oauth2_credentials_pkey PRIMARY KEY (id);


--
-- Name: oauth2_credentials oauth2_credentials_ws_id_client_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_credentials
    ADD CONSTRAINT oauth2_credentials_ws_id_client_id_unique UNIQUE (ws_id, client_id);


--
-- Name: oauth2_tokens oauth2_tokens_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_tokens
    ADD CONSTRAINT oauth2_tokens_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: oauth2_tokens oauth2_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_tokens
    ADD CONSTRAINT oauth2_tokens_pkey PRIMARY KEY (id);


--
-- Name: oauth2_tokens oauth2_tokens_ws_id_access_token_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_tokens
    ADD CONSTRAINT oauth2_tokens_ws_id_access_token_unique UNIQUE (ws_id, access_token);


--
-- Name: oauth2_tokens oauth2_tokens_ws_id_refresh_token_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_tokens
    ADD CONSTRAINT oauth2_tokens_ws_id_refresh_token_unique UNIQUE (ws_id, refresh_token);


--
-- Name: parameters parameters_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.parameters
    ADD CONSTRAINT parameters_pkey PRIMARY KEY (key);


--
-- Name: plugins plugins_cache_key_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.plugins
    ADD CONSTRAINT plugins_cache_key_key UNIQUE (cache_key);


--
-- Name: plugins plugins_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.plugins
    ADD CONSTRAINT plugins_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: plugins plugins_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.plugins
    ADD CONSTRAINT plugins_pkey PRIMARY KEY (id);


--
-- Name: plugins plugins_ws_id_instance_name_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.plugins
    ADD CONSTRAINT plugins_ws_id_instance_name_unique UNIQUE (ws_id, instance_name);


--
-- Name: ratelimiting_metrics ratelimiting_metrics_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.ratelimiting_metrics
    ADD CONSTRAINT ratelimiting_metrics_pkey PRIMARY KEY (identifier, period, period_date, service_id, route_id);


--
-- Name: response_ratelimiting_metrics response_ratelimiting_metrics_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.response_ratelimiting_metrics
    ADD CONSTRAINT response_ratelimiting_metrics_pkey PRIMARY KEY (identifier, period, period_date, service_id, route_id);


--
-- Name: routes routes_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.routes
    ADD CONSTRAINT routes_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: routes routes_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.routes
    ADD CONSTRAINT routes_pkey PRIMARY KEY (id);


--
-- Name: routes routes_ws_id_name_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.routes
    ADD CONSTRAINT routes_ws_id_name_unique UNIQUE (ws_id, name);


--
-- Name: schema_meta schema_meta_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.schema_meta
    ADD CONSTRAINT schema_meta_pkey PRIMARY KEY (key, subsystem);


--
-- Name: services services_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.services
    ADD CONSTRAINT services_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: services services_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.services
    ADD CONSTRAINT services_pkey PRIMARY KEY (id);


--
-- Name: services services_ws_id_name_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.services
    ADD CONSTRAINT services_ws_id_name_unique UNIQUE (ws_id, name);


--
-- Name: sessions sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.sessions
    ADD CONSTRAINT sessions_pkey PRIMARY KEY (id);


--
-- Name: sessions sessions_session_id_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.sessions
    ADD CONSTRAINT sessions_session_id_key UNIQUE (session_id);


--
-- Name: sm_vaults sm_vaults_id_ws_id_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.sm_vaults
    ADD CONSTRAINT sm_vaults_id_ws_id_key UNIQUE (id, ws_id);


--
-- Name: sm_vaults sm_vaults_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.sm_vaults
    ADD CONSTRAINT sm_vaults_pkey PRIMARY KEY (id);


--
-- Name: sm_vaults sm_vaults_prefix_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.sm_vaults
    ADD CONSTRAINT sm_vaults_prefix_key UNIQUE (prefix);


--
-- Name: sm_vaults sm_vaults_prefix_ws_id_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.sm_vaults
    ADD CONSTRAINT sm_vaults_prefix_ws_id_key UNIQUE (prefix, ws_id);


--
-- Name: snis snis_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.snis
    ADD CONSTRAINT snis_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: snis snis_name_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.snis
    ADD CONSTRAINT snis_name_key UNIQUE (name);


--
-- Name: snis snis_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.snis
    ADD CONSTRAINT snis_pkey PRIMARY KEY (id);


--
-- Name: tags tags_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT tags_pkey PRIMARY KEY (entity_id);


--
-- Name: targets targets_cache_key_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.targets
    ADD CONSTRAINT targets_cache_key_key UNIQUE (cache_key);


--
-- Name: targets targets_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.targets
    ADD CONSTRAINT targets_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: targets targets_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.targets
    ADD CONSTRAINT targets_pkey PRIMARY KEY (id);


--
-- Name: upstreams upstreams_id_ws_id_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.upstreams
    ADD CONSTRAINT upstreams_id_ws_id_unique UNIQUE (id, ws_id);


--
-- Name: upstreams upstreams_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.upstreams
    ADD CONSTRAINT upstreams_pkey PRIMARY KEY (id);


--
-- Name: upstreams upstreams_ws_id_name_unique; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.upstreams
    ADD CONSTRAINT upstreams_ws_id_name_unique UNIQUE (ws_id, name);


--
-- Name: workspaces workspaces_name_key; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.workspaces
    ADD CONSTRAINT workspaces_name_key UNIQUE (name);


--
-- Name: workspaces workspaces_pkey; Type: CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.workspaces
    ADD CONSTRAINT workspaces_pkey PRIMARY KEY (id);


--
-- Name: acls_consumer_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX acls_consumer_id_idx ON public.acls USING btree (consumer_id);


--
-- Name: acls_group_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX acls_group_idx ON public.acls USING btree ("group");


--
-- Name: acls_tags_idex_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX acls_tags_idex_tags_idx ON public.acls USING gin (tags);


--
-- Name: acme_storage_ttl_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX acme_storage_ttl_idx ON public.acme_storage USING btree (ttl);


--
-- Name: basicauth_consumer_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX basicauth_consumer_id_idx ON public.basicauth_credentials USING btree (consumer_id);


--
-- Name: basicauth_tags_idex_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX basicauth_tags_idex_tags_idx ON public.basicauth_credentials USING gin (tags);


--
-- Name: certificates_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX certificates_tags_idx ON public.certificates USING gin (tags);


--
-- Name: cluster_events_at_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX cluster_events_at_idx ON public.cluster_events USING btree (at);


--
-- Name: cluster_events_channel_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX cluster_events_channel_idx ON public.cluster_events USING btree (channel);


--
-- Name: cluster_events_expire_at_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX cluster_events_expire_at_idx ON public.cluster_events USING btree (expire_at);


--
-- Name: clustering_data_planes_ttl_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX clustering_data_planes_ttl_idx ON public.clustering_data_planes USING btree (ttl);


--
-- Name: clustering_rpc_requests_node_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX clustering_rpc_requests_node_id_idx ON public.clustering_rpc_requests USING btree (node_id);


--
-- Name: clustering_sync_delta_version_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX clustering_sync_delta_version_idx ON public.clustering_sync_delta USING btree (version);


--
-- Name: consumers_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX consumers_tags_idx ON public.consumers USING gin (tags);


--
-- Name: consumers_username_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX consumers_username_idx ON public.consumers USING btree (lower(username));


--
-- Name: filter_chains_cache_key_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE UNIQUE INDEX filter_chains_cache_key_idx ON public.filter_chains USING btree (cache_key);


--
-- Name: filter_chains_name_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE UNIQUE INDEX filter_chains_name_idx ON public.filter_chains USING btree (name);


--
-- Name: filter_chains_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX filter_chains_tags_idx ON public.filter_chains USING gin (tags);


--
-- Name: hmacauth_credentials_consumer_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX hmacauth_credentials_consumer_id_idx ON public.hmacauth_credentials USING btree (consumer_id);


--
-- Name: hmacauth_tags_idex_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX hmacauth_tags_idex_tags_idx ON public.hmacauth_credentials USING gin (tags);


--
-- Name: jwt_secrets_consumer_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX jwt_secrets_consumer_id_idx ON public.jwt_secrets USING btree (consumer_id);


--
-- Name: jwt_secrets_secret_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX jwt_secrets_secret_idx ON public.jwt_secrets USING btree (secret);


--
-- Name: jwtsecrets_tags_idex_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX jwtsecrets_tags_idex_tags_idx ON public.jwt_secrets USING gin (tags);


--
-- Name: key_sets_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX key_sets_tags_idx ON public.key_sets USING gin (tags);


--
-- Name: keyauth_credentials_consumer_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX keyauth_credentials_consumer_id_idx ON public.keyauth_credentials USING btree (consumer_id);


--
-- Name: keyauth_credentials_ttl_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX keyauth_credentials_ttl_idx ON public.keyauth_credentials USING btree (ttl);


--
-- Name: keyauth_tags_idex_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX keyauth_tags_idex_tags_idx ON public.keyauth_credentials USING gin (tags);


--
-- Name: keys_fkey_key_sets; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX keys_fkey_key_sets ON public.keys USING btree (set_id);


--
-- Name: keys_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX keys_tags_idx ON public.keys USING gin (tags);


--
-- Name: locks_ttl_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX locks_ttl_idx ON public.locks USING btree (ttl);


--
-- Name: oauth2_authorization_codes_authenticated_userid_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_authorization_codes_authenticated_userid_idx ON public.oauth2_authorization_codes USING btree (authenticated_userid);


--
-- Name: oauth2_authorization_codes_ttl_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_authorization_codes_ttl_idx ON public.oauth2_authorization_codes USING btree (ttl);


--
-- Name: oauth2_authorization_credential_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_authorization_credential_id_idx ON public.oauth2_authorization_codes USING btree (credential_id);


--
-- Name: oauth2_authorization_service_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_authorization_service_id_idx ON public.oauth2_authorization_codes USING btree (service_id);


--
-- Name: oauth2_credentials_consumer_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_credentials_consumer_id_idx ON public.oauth2_credentials USING btree (consumer_id);


--
-- Name: oauth2_credentials_secret_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_credentials_secret_idx ON public.oauth2_credentials USING btree (client_secret);


--
-- Name: oauth2_credentials_tags_idex_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_credentials_tags_idex_tags_idx ON public.oauth2_credentials USING gin (tags);


--
-- Name: oauth2_tokens_authenticated_userid_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_tokens_authenticated_userid_idx ON public.oauth2_tokens USING btree (authenticated_userid);


--
-- Name: oauth2_tokens_credential_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_tokens_credential_id_idx ON public.oauth2_tokens USING btree (credential_id);


--
-- Name: oauth2_tokens_service_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_tokens_service_id_idx ON public.oauth2_tokens USING btree (service_id);


--
-- Name: oauth2_tokens_ttl_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX oauth2_tokens_ttl_idx ON public.oauth2_tokens USING btree (ttl);


--
-- Name: plugins_consumer_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX plugins_consumer_id_idx ON public.plugins USING btree (consumer_id);


--
-- Name: plugins_name_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX plugins_name_idx ON public.plugins USING btree (name);


--
-- Name: plugins_route_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX plugins_route_id_idx ON public.plugins USING btree (route_id);


--
-- Name: plugins_service_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX plugins_service_id_idx ON public.plugins USING btree (service_id);


--
-- Name: plugins_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX plugins_tags_idx ON public.plugins USING gin (tags);


--
-- Name: ratelimiting_metrics_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX ratelimiting_metrics_idx ON public.ratelimiting_metrics USING btree (service_id, route_id, period_date, period);


--
-- Name: ratelimiting_metrics_ttl_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX ratelimiting_metrics_ttl_idx ON public.ratelimiting_metrics USING btree (ttl);


--
-- Name: routes_service_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX routes_service_id_idx ON public.routes USING btree (service_id);


--
-- Name: routes_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX routes_tags_idx ON public.routes USING gin (tags);


--
-- Name: services_fkey_client_certificate; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX services_fkey_client_certificate ON public.services USING btree (client_certificate_id);


--
-- Name: services_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX services_tags_idx ON public.services USING gin (tags);


--
-- Name: session_sessions_expires_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX session_sessions_expires_idx ON public.sessions USING btree (expires);


--
-- Name: sessions_ttl_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX sessions_ttl_idx ON public.sessions USING btree (ttl);


--
-- Name: sm_vaults_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX sm_vaults_tags_idx ON public.sm_vaults USING gin (tags);


--
-- Name: snis_certificate_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX snis_certificate_id_idx ON public.snis USING btree (certificate_id);


--
-- Name: snis_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX snis_tags_idx ON public.snis USING gin (tags);


--
-- Name: tags_entity_name_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX tags_entity_name_idx ON public.tags USING btree (entity_name);


--
-- Name: tags_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX tags_tags_idx ON public.tags USING gin (tags);


--
-- Name: targets_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX targets_tags_idx ON public.targets USING gin (tags);


--
-- Name: targets_target_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX targets_target_idx ON public.targets USING btree (target);


--
-- Name: targets_upstream_id_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX targets_upstream_id_idx ON public.targets USING btree (upstream_id);


--
-- Name: upstreams_fkey_client_certificate; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX upstreams_fkey_client_certificate ON public.upstreams USING btree (client_certificate_id);


--
-- Name: upstreams_tags_idx; Type: INDEX; Schema: public; Owner: lreas
--

CREATE INDEX upstreams_tags_idx ON public.upstreams USING gin (tags);


--
-- Name: acls acls_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER acls_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.acls FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: acme_storage acme_storage_ttl_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER acme_storage_ttl_trigger AFTER INSERT ON public.acme_storage FOR EACH STATEMENT EXECUTE FUNCTION public.batch_delete_expired_rows('ttl');


--
-- Name: basicauth_credentials basicauth_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER basicauth_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.basicauth_credentials FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: ca_certificates ca_certificates_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER ca_certificates_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.ca_certificates FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: certificates certificates_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER certificates_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.certificates FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: cluster_events cluster_events_ttl_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER cluster_events_ttl_trigger AFTER INSERT ON public.cluster_events FOR EACH STATEMENT EXECUTE FUNCTION public.batch_delete_expired_rows('expire_at');


--
-- Name: clustering_data_planes clustering_data_planes_ttl_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER clustering_data_planes_ttl_trigger AFTER INSERT ON public.clustering_data_planes FOR EACH STATEMENT EXECUTE FUNCTION public.batch_delete_expired_rows('ttl');


--
-- Name: consumers consumers_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER consumers_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.consumers FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: filter_chains filter_chains_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER filter_chains_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.filter_chains FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: hmacauth_credentials hmacauth_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER hmacauth_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.hmacauth_credentials FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: jwt_secrets jwtsecrets_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER jwtsecrets_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.jwt_secrets FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: key_sets key_sets_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER key_sets_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.key_sets FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: keyauth_credentials keyauth_credentials_ttl_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER keyauth_credentials_ttl_trigger AFTER INSERT ON public.keyauth_credentials FOR EACH STATEMENT EXECUTE FUNCTION public.batch_delete_expired_rows('ttl');


--
-- Name: keyauth_credentials keyauth_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER keyauth_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.keyauth_credentials FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: keys keys_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER keys_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.keys FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: oauth2_authorization_codes oauth2_authorization_codes_ttl_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER oauth2_authorization_codes_ttl_trigger AFTER INSERT ON public.oauth2_authorization_codes FOR EACH STATEMENT EXECUTE FUNCTION public.batch_delete_expired_rows('ttl');


--
-- Name: oauth2_credentials oauth2_credentials_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER oauth2_credentials_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.oauth2_credentials FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: oauth2_tokens oauth2_tokens_ttl_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER oauth2_tokens_ttl_trigger AFTER INSERT ON public.oauth2_tokens FOR EACH STATEMENT EXECUTE FUNCTION public.batch_delete_expired_rows('ttl');


--
-- Name: plugins plugins_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER plugins_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.plugins FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: ratelimiting_metrics ratelimiting_metrics_ttl_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER ratelimiting_metrics_ttl_trigger AFTER INSERT ON public.ratelimiting_metrics FOR EACH STATEMENT EXECUTE FUNCTION public.batch_delete_expired_rows('ttl');


--
-- Name: routes routes_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER routes_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.routes FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: services services_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER services_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.services FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: sessions sessions_ttl_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER sessions_ttl_trigger AFTER INSERT ON public.sessions FOR EACH STATEMENT EXECUTE FUNCTION public.batch_delete_expired_rows('ttl');


--
-- Name: sm_vaults sm_vaults_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER sm_vaults_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.sm_vaults FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: snis snis_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER snis_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.snis FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: targets targets_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER targets_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.targets FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: upstreams upstreams_sync_tags_trigger; Type: TRIGGER; Schema: public; Owner: lreas
--

CREATE TRIGGER upstreams_sync_tags_trigger AFTER INSERT OR DELETE OR UPDATE OF tags ON public.upstreams FOR EACH ROW EXECUTE FUNCTION public.sync_tags();


--
-- Name: acls acls_consumer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.acls
    ADD CONSTRAINT acls_consumer_id_fkey FOREIGN KEY (consumer_id, ws_id) REFERENCES public.consumers(id, ws_id) ON DELETE CASCADE;


--
-- Name: acls acls_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.acls
    ADD CONSTRAINT acls_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: basicauth_credentials basicauth_credentials_consumer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.basicauth_credentials
    ADD CONSTRAINT basicauth_credentials_consumer_id_fkey FOREIGN KEY (consumer_id, ws_id) REFERENCES public.consumers(id, ws_id) ON DELETE CASCADE;


--
-- Name: basicauth_credentials basicauth_credentials_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.basicauth_credentials
    ADD CONSTRAINT basicauth_credentials_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: certificates certificates_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.certificates
    ADD CONSTRAINT certificates_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: clustering_sync_delta clustering_sync_delta_version_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.clustering_sync_delta
    ADD CONSTRAINT clustering_sync_delta_version_fkey FOREIGN KEY (version) REFERENCES public.clustering_sync_version(version) ON DELETE CASCADE;


--
-- Name: consumers consumers_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.consumers
    ADD CONSTRAINT consumers_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: filter_chains filter_chains_route_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.filter_chains
    ADD CONSTRAINT filter_chains_route_id_fkey FOREIGN KEY (route_id) REFERENCES public.routes(id) ON DELETE CASCADE;


--
-- Name: filter_chains filter_chains_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.filter_chains
    ADD CONSTRAINT filter_chains_service_id_fkey FOREIGN KEY (service_id) REFERENCES public.services(id) ON DELETE CASCADE;


--
-- Name: filter_chains filter_chains_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.filter_chains
    ADD CONSTRAINT filter_chains_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id) ON DELETE CASCADE;


--
-- Name: hmacauth_credentials hmacauth_credentials_consumer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.hmacauth_credentials
    ADD CONSTRAINT hmacauth_credentials_consumer_id_fkey FOREIGN KEY (consumer_id, ws_id) REFERENCES public.consumers(id, ws_id) ON DELETE CASCADE;


--
-- Name: hmacauth_credentials hmacauth_credentials_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.hmacauth_credentials
    ADD CONSTRAINT hmacauth_credentials_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: jwt_secrets jwt_secrets_consumer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.jwt_secrets
    ADD CONSTRAINT jwt_secrets_consumer_id_fkey FOREIGN KEY (consumer_id, ws_id) REFERENCES public.consumers(id, ws_id) ON DELETE CASCADE;


--
-- Name: jwt_secrets jwt_secrets_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.jwt_secrets
    ADD CONSTRAINT jwt_secrets_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: key_sets key_sets_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.key_sets
    ADD CONSTRAINT key_sets_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: keyauth_credentials keyauth_credentials_consumer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keyauth_credentials
    ADD CONSTRAINT keyauth_credentials_consumer_id_fkey FOREIGN KEY (consumer_id, ws_id) REFERENCES public.consumers(id, ws_id) ON DELETE CASCADE;


--
-- Name: keyauth_credentials keyauth_credentials_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keyauth_credentials
    ADD CONSTRAINT keyauth_credentials_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: keys keys_set_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keys
    ADD CONSTRAINT keys_set_id_fkey FOREIGN KEY (set_id) REFERENCES public.key_sets(id) ON DELETE CASCADE;


--
-- Name: keys keys_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.keys
    ADD CONSTRAINT keys_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: oauth2_authorization_codes oauth2_authorization_codes_credential_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_authorization_codes
    ADD CONSTRAINT oauth2_authorization_codes_credential_id_fkey FOREIGN KEY (credential_id, ws_id) REFERENCES public.oauth2_credentials(id, ws_id) ON DELETE CASCADE;


--
-- Name: oauth2_authorization_codes oauth2_authorization_codes_plugin_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_authorization_codes
    ADD CONSTRAINT oauth2_authorization_codes_plugin_id_fkey FOREIGN KEY (plugin_id) REFERENCES public.plugins(id) ON DELETE CASCADE;


--
-- Name: oauth2_authorization_codes oauth2_authorization_codes_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_authorization_codes
    ADD CONSTRAINT oauth2_authorization_codes_service_id_fkey FOREIGN KEY (service_id, ws_id) REFERENCES public.services(id, ws_id) ON DELETE CASCADE;


--
-- Name: oauth2_authorization_codes oauth2_authorization_codes_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_authorization_codes
    ADD CONSTRAINT oauth2_authorization_codes_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: oauth2_credentials oauth2_credentials_consumer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_credentials
    ADD CONSTRAINT oauth2_credentials_consumer_id_fkey FOREIGN KEY (consumer_id, ws_id) REFERENCES public.consumers(id, ws_id) ON DELETE CASCADE;


--
-- Name: oauth2_credentials oauth2_credentials_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_credentials
    ADD CONSTRAINT oauth2_credentials_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: oauth2_tokens oauth2_tokens_credential_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_tokens
    ADD CONSTRAINT oauth2_tokens_credential_id_fkey FOREIGN KEY (credential_id, ws_id) REFERENCES public.oauth2_credentials(id, ws_id) ON DELETE CASCADE;


--
-- Name: oauth2_tokens oauth2_tokens_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_tokens
    ADD CONSTRAINT oauth2_tokens_service_id_fkey FOREIGN KEY (service_id, ws_id) REFERENCES public.services(id, ws_id) ON DELETE CASCADE;


--
-- Name: oauth2_tokens oauth2_tokens_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.oauth2_tokens
    ADD CONSTRAINT oauth2_tokens_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: plugins plugins_consumer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.plugins
    ADD CONSTRAINT plugins_consumer_id_fkey FOREIGN KEY (consumer_id, ws_id) REFERENCES public.consumers(id, ws_id) ON DELETE CASCADE;


--
-- Name: plugins plugins_route_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.plugins
    ADD CONSTRAINT plugins_route_id_fkey FOREIGN KEY (route_id, ws_id) REFERENCES public.routes(id, ws_id) ON DELETE CASCADE;


--
-- Name: plugins plugins_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.plugins
    ADD CONSTRAINT plugins_service_id_fkey FOREIGN KEY (service_id, ws_id) REFERENCES public.services(id, ws_id) ON DELETE CASCADE;


--
-- Name: plugins plugins_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.plugins
    ADD CONSTRAINT plugins_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: routes routes_service_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.routes
    ADD CONSTRAINT routes_service_id_fkey FOREIGN KEY (service_id, ws_id) REFERENCES public.services(id, ws_id);


--
-- Name: routes routes_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.routes
    ADD CONSTRAINT routes_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: services services_client_certificate_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.services
    ADD CONSTRAINT services_client_certificate_id_fkey FOREIGN KEY (client_certificate_id, ws_id) REFERENCES public.certificates(id, ws_id);


--
-- Name: services services_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.services
    ADD CONSTRAINT services_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: sm_vaults sm_vaults_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.sm_vaults
    ADD CONSTRAINT sm_vaults_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: snis snis_certificate_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.snis
    ADD CONSTRAINT snis_certificate_id_fkey FOREIGN KEY (certificate_id, ws_id) REFERENCES public.certificates(id, ws_id);


--
-- Name: snis snis_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.snis
    ADD CONSTRAINT snis_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: targets targets_upstream_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.targets
    ADD CONSTRAINT targets_upstream_id_fkey FOREIGN KEY (upstream_id, ws_id) REFERENCES public.upstreams(id, ws_id) ON DELETE CASCADE;


--
-- Name: targets targets_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.targets
    ADD CONSTRAINT targets_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: upstreams upstreams_client_certificate_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.upstreams
    ADD CONSTRAINT upstreams_client_certificate_id_fkey FOREIGN KEY (client_certificate_id) REFERENCES public.certificates(id);


--
-- Name: upstreams upstreams_ws_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: lreas
--

ALTER TABLE ONLY public.upstreams
    ADD CONSTRAINT upstreams_ws_id_fkey FOREIGN KEY (ws_id) REFERENCES public.workspaces(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT ALL ON SCHEMA public TO lreas;


--
-- PostgreSQL database dump complete
--

