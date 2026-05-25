create table if not exists spotify_user_tokens (
    id bigserial primary key,
    telegram_user_id bigint not null unique,
    spotify_user_id varchar(255),
    access_token varchar(2048) not null,
    refresh_token varchar(2048) not null,
    expires_at timestamp with time zone
);

create table if not exists discovered_playlists (
    id bigserial primary key,
    spotify_playlist_id varchar(255) not null unique,
    name varchar(255),
    owner_name varchar(255),
    spotify_url varchar(1024),
    query_seed varchar(255),
    usage_count integer,
    discovered_at timestamp with time zone,
    last_served_at timestamp with time zone,
    language varchar(64),
    active boolean
);

create index if not exists idx_discovered_playlists_active on discovered_playlists (active);
create index if not exists idx_discovered_playlists_usage_count on discovered_playlists (usage_count);
create index if not exists idx_discovered_playlists_last_served_at on discovered_playlists (last_served_at);
create index if not exists idx_discovered_playlists_discovered_at on discovered_playlists (discovered_at);
create index if not exists idx_discovered_playlists_query_seed on discovered_playlists (query_seed);

create table if not exists user_taste_profiles (
    id bigserial primary key,
    telegram_user_id bigint not null unique,
    spotify_user_id varchar(255),
    randomness_level integer not null,
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    last_synced_at timestamp with time zone
);

create index if not exists idx_user_taste_profiles_last_synced_at on user_taste_profiles (last_synced_at);

create table if not exists user_taste_top_artists (
    id bigserial primary key,
    telegram_user_id bigint not null,
    time_range varchar(64) not null,
    rank_position integer not null,
    artist_id varchar(255) not null,
    artist_name varchar(255),
    popularity integer,
    genres varchar(2048),
    synced_at timestamp with time zone
);

create index if not exists idx_user_taste_top_artists_user_range on user_taste_top_artists (telegram_user_id, time_range);
create index if not exists idx_user_taste_top_artists_artist_id on user_taste_top_artists (artist_id);

create table if not exists user_taste_top_tracks (
    id bigserial primary key,
    telegram_user_id bigint not null,
    time_range varchar(64) not null,
    rank_position integer not null,
    track_id varchar(255) not null,
    track_name varchar(255),
    primary_artist_id varchar(255),
    primary_artist_name varchar(255),
    album_name varchar(255),
    popularity integer,
    explicit_track boolean,
    synced_at timestamp with time zone
);

create index if not exists idx_user_taste_top_tracks_user_range on user_taste_top_tracks (telegram_user_id, time_range);
create index if not exists idx_user_taste_top_tracks_track_id on user_taste_top_tracks (track_id);
create index if not exists idx_user_taste_top_tracks_primary_artist_id on user_taste_top_tracks (primary_artist_id);

create table if not exists user_liked_tracks (
    id bigserial primary key,
    telegram_user_id bigint not null,
    track_id varchar(255) not null,
    track_name varchar(255),
    primary_artist_id varchar(255),
    primary_artist_name varchar(255),
    album_name varchar(255),
    popularity integer,
    explicit_track boolean,
    added_at timestamp with time zone,
    synced_at timestamp with time zone
);

create unique index if not exists idx_user_liked_tracks_user_track on user_liked_tracks (telegram_user_id, track_id);
create index if not exists idx_user_liked_tracks_primary_artist_id on user_liked_tracks (primary_artist_id);
create index if not exists idx_user_liked_tracks_added_at on user_liked_tracks (added_at);
