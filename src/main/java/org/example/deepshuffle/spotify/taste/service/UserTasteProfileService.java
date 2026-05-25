package org.example.deepshuffle.spotify.taste.service;

import lombok.RequiredArgsConstructor;
import org.example.deepshuffle.service.SpotifyTokenService;
import org.example.deepshuffle.spotify.auth.exception.SpotifyAuthorizationRequiredException;
import org.example.deepshuffle.spotify.auth.model.SpotifyUserToken;
import org.example.deepshuffle.spotify.taste.client.SpotifyTasteClient;
import org.example.deepshuffle.spotify.taste.dto.SpotifyArtistItem;
import org.example.deepshuffle.spotify.taste.dto.SpotifySavedTrackItem;
import org.example.deepshuffle.spotify.taste.dto.SpotifySavedTracksResponse;
import org.example.deepshuffle.spotify.taste.dto.SpotifySimplifiedArtistItem;
import org.example.deepshuffle.spotify.taste.dto.SpotifyTrackItem;
import org.example.deepshuffle.spotify.taste.entity.UserLikedTrackEntity;
import org.example.deepshuffle.spotify.taste.entity.UserTasteProfileEntity;
import org.example.deepshuffle.spotify.taste.entity.UserTasteTopArtistEntity;
import org.example.deepshuffle.spotify.taste.entity.UserTasteTopTrackEntity;
import org.example.deepshuffle.spotify.taste.model.TasteTimeRange;
import org.example.deepshuffle.spotify.taste.model.UserTasteSnapshot;
import org.example.deepshuffle.spotify.taste.repository.UserLikedTrackRepository;
import org.example.deepshuffle.spotify.taste.repository.UserTasteProfileRepository;
import org.example.deepshuffle.spotify.taste.repository.UserTasteTopArtistRepository;
import org.example.deepshuffle.spotify.taste.repository.UserTasteTopTrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserTasteProfileService {

    private static final int DEFAULT_RANDOMNESS_LEVEL = 50;
    private static final int TOP_ITEM_LIMIT = 50;
    private static final int LIKED_TRACK_LIMIT = 200;

    private final SpotifyTokenService tokenService;
    private final SpotifyTasteClient tasteClient;
    private final UserTasteProfileRepository profileRepository;
    private final UserTasteTopArtistRepository topArtistRepository;
    private final UserTasteTopTrackRepository topTrackRepository;
    private final UserLikedTrackRepository likedTrackRepository;

    public int getRandomnessLevel(Long telegramUserId) {
        return profileRepository.findByTelegramUserId(telegramUserId)
                .map(UserTasteProfileEntity::getRandomnessLevel)
                .orElse(DEFAULT_RANDOMNESS_LEVEL);
    }

    public int updateRandomnessLevel(Long telegramUserId, int randomnessLevel) {
        int normalizedLevel = Math.max(0, Math.min(100, randomnessLevel));
        Instant now = Instant.now();

        UserTasteProfileEntity profile = profileRepository.findByTelegramUserId(telegramUserId)
                .orElseGet(() -> newProfile(telegramUserId));
        profile.setRandomnessLevel(normalizedLevel);
        profile.setUpdatedAt(now);

        return profileRepository.save(profile).getRandomnessLevel();
    }

    @Transactional
    public UserTasteSnapshot syncTasteSnapshot(Long telegramUserId) {
        SpotifyUserToken token = tokenService.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new SpotifyAuthorizationRequiredException("Spotify account is not connected"));
        String accessToken = tokenService.getValidAccessToken(telegramUserId);
        Instant syncedAt = Instant.now();

        UserTasteProfileEntity profile = profileRepository.findByTelegramUserId(telegramUserId)
                .orElseGet(() -> newProfile(telegramUserId));
        profile.setSpotifyUserId(token.getSpotifyUserId());
        profile.setUpdatedAt(syncedAt);
        profile.setLastSyncedAt(syncedAt);
        profileRepository.save(profile);

        int topArtistCount;
        int topTrackCount;
        int likedTrackCount;
        try {
            topArtistCount = syncTopArtists(telegramUserId, accessToken, syncedAt);
            topTrackCount = syncTopTracks(telegramUserId, accessToken, syncedAt);
            likedTrackCount = syncLikedTracks(telegramUserId, accessToken, syncedAt);
        } catch (WebClientResponseException.Unauthorized | WebClientResponseException.Forbidden e) {
            throw new SpotifyAuthorizationRequiredException("Spotify reconnect is required for taste fingerprint");
        }

        return UserTasteSnapshot.builder()
                .telegramUserId(telegramUserId)
                .topArtistsCount(topArtistCount)
                .topTracksCount(topTrackCount)
                .likedTracksCount(likedTrackCount)
                .randomnessLevel(profile.getRandomnessLevel())
                .build();
    }

    private int syncTopArtists(Long telegramUserId, String accessToken, Instant syncedAt) {
        int savedCount = 0;
        for (TasteTimeRange timeRange : TasteTimeRange.values()) {
            List<SpotifyArtistItem> artists = tasteClient.getTopArtists(accessToken, timeRange, TOP_ITEM_LIMIT)
                    .stream()
                    .filter(artist -> artist.id() != null)
                    .toList();

            topArtistRepository.deleteByTelegramUserIdAndTimeRange(telegramUserId, timeRange.spotifyValue());
            for (int index = 0; index < artists.size(); index++) {
                topArtistRepository.save(toTopArtistEntity(telegramUserId, timeRange, artists.get(index), index, syncedAt));
            }
            savedCount += artists.size();
        }

        return savedCount;
    }

    private int syncTopTracks(Long telegramUserId, String accessToken, Instant syncedAt) {
        int savedCount = 0;
        for (TasteTimeRange timeRange : TasteTimeRange.values()) {
            List<SpotifyTrackItem> tracks = tasteClient.getTopTracks(accessToken, timeRange, TOP_ITEM_LIMIT)
                    .stream()
                    .filter(track -> track.id() != null)
                    .toList();

            topTrackRepository.deleteByTelegramUserIdAndTimeRange(telegramUserId, timeRange.spotifyValue());
            for (int index = 0; index < tracks.size(); index++) {
                topTrackRepository.save(toTopTrackEntity(telegramUserId, timeRange, tracks.get(index), index, syncedAt));
            }
            savedCount += tracks.size();
        }

        return savedCount;
    }

    private int syncLikedTracks(Long telegramUserId, String accessToken, Instant syncedAt) {
        List<SpotifySavedTrackItem> likedTracks = tasteClient.getSavedTracksPages(accessToken, LIKED_TRACK_LIMIT)
                .stream()
                .map(SpotifySavedTracksResponse::items)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(savedTrack -> savedTrack.track() != null && savedTrack.track().id() != null)
                .toList();

        for (SpotifySavedTrackItem savedTrack : likedTracks) {
            UserLikedTrackEntity entity = likedTrackRepository
                    .findByTelegramUserIdAndTrackId(telegramUserId, savedTrack.track().id())
                    .orElseGet(() -> UserLikedTrackEntity.builder()
                            .telegramUserId(telegramUserId)
                            .trackId(savedTrack.track().id())
                            .build());

            updateLikedTrack(entity, savedTrack, syncedAt);
            likedTrackRepository.save(entity);
        }

        return likedTracks.size();
    }

    private UserTasteProfileEntity newProfile(Long telegramUserId) {
        Instant now = Instant.now();
        return UserTasteProfileEntity.builder()
                .telegramUserId(telegramUserId)
                .randomnessLevel(DEFAULT_RANDOMNESS_LEVEL)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private UserTasteTopArtistEntity toTopArtistEntity(Long telegramUserId,
                                                       TasteTimeRange timeRange,
                                                       SpotifyArtistItem artist,
                                                       int index,
                                                       Instant syncedAt) {
        return UserTasteTopArtistEntity.builder()
                .telegramUserId(telegramUserId)
                .timeRange(timeRange.spotifyValue())
                .rankPosition(index + 1)
                .artistId(artist.id())
                .artistName(artist.name())
                .popularity(artist.popularity())
                .genres(joinGenres(artist.genres()))
                .syncedAt(syncedAt)
                .build();
    }

    private UserTasteTopTrackEntity toTopTrackEntity(Long telegramUserId,
                                                     TasteTimeRange timeRange,
                                                     SpotifyTrackItem track,
                                                     int index,
                                                     Instant syncedAt) {
        SpotifySimplifiedArtistItem primaryArtist = primaryArtist(track);
        return UserTasteTopTrackEntity.builder()
                .telegramUserId(telegramUserId)
                .timeRange(timeRange.spotifyValue())
                .rankPosition(index + 1)
                .trackId(track.id())
                .trackName(track.name())
                .primaryArtistId(primaryArtist == null ? null : primaryArtist.id())
                .primaryArtistName(primaryArtist == null ? null : primaryArtist.name())
                .albumName(track.album() == null ? null : track.album().name())
                .popularity(track.popularity())
                .explicitTrack(track.explicitTrack())
                .syncedAt(syncedAt)
                .build();
    }

    private void updateLikedTrack(UserLikedTrackEntity entity, SpotifySavedTrackItem savedTrack, Instant syncedAt) {
        SpotifyTrackItem track = savedTrack.track();
        SpotifySimplifiedArtistItem primaryArtist = primaryArtist(track);

        entity.setTrackName(track.name());
        entity.setPrimaryArtistId(primaryArtist == null ? null : primaryArtist.id());
        entity.setPrimaryArtistName(primaryArtist == null ? null : primaryArtist.name());
        entity.setAlbumName(track.album() == null ? null : track.album().name());
        entity.setPopularity(track.popularity());
        entity.setExplicitTrack(track.explicitTrack());
        entity.setAddedAt(savedTrack.addedAt());
        entity.setSyncedAt(syncedAt);
    }

    private SpotifySimplifiedArtistItem primaryArtist(SpotifyTrackItem track) {
        if (track.artists() == null || track.artists().isEmpty()) {
            return null;
        }
        return track.artists().getFirst();
    }

    private String joinGenres(List<String> genres) {
        if (genres == null || genres.isEmpty()) {
            return null;
        }
        return String.join(",", genres);
    }
}
