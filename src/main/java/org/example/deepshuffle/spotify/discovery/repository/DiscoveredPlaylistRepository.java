package org.example.deepshuffle.spotify.discovery.repository;

import org.example.deepshuffle.spotify.discovery.entity.DiscoveredPlaylistEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiscoveredPlaylistRepository extends JpaRepository<DiscoveredPlaylistEntity, Long> {

    Optional<DiscoveredPlaylistEntity> findBySpotifyPlaylistId(String spotifyPlaylistId);

    long countByActiveTrue();

    @Query("""
            select playlist
            from DiscoveredPlaylistEntity playlist
            where playlist.active = true
              and (
                  lower(playlist.querySeed) like lower(concat('%', :query, '%'))
                  or lower(playlist.name) like lower(concat('%', :query, '%'))
              )
            order by playlist.discoveredAt desc
            """)
    List<DiscoveredPlaylistEntity> findCachedByQuery(@Param("query") String query, Pageable pageable);

    @Query("""
            select playlist
            from DiscoveredPlaylistEntity playlist
            where playlist.active = true
            order by
                coalesce(playlist.usageCount, 0) asc,
                playlist.lastServedAt asc nulls first,
                playlist.discoveredAt desc
            """)
    List<DiscoveredPlaylistEntity> findWeightedCandidates(Pageable pageable);
}
