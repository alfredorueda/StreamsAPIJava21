package com.streamexercises.service;

import com.streamexercises.model.*;

import java.time.Duration;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class containing 10 advanced exercises for mastering Java 21 Streams API,
 * lambda expressions, and method references.
 */
public class MusicAnalyticsService {

    /**
     * Exercise 1: Group songs by genre and find the average popularity for each genre.
     * 
     * Uses:
     * - collect with groupingBy
     * - averaging downstream collector
     * - method references
     */
    public Map<Genre, Double> getAveragePopularityByGenre(List<Song> songs) {
        return songs.stream()
                .collect(Collectors.groupingBy(
                        Song::getPrimaryGenre,
                        Collectors.averagingDouble(Song::getPopularity)
                ));
    }

    /**
     * Exercise 2: Find the most popular song for each genre.
     * 
     * Uses:
     * - collect with groupingBy
     * - maxBy downstream collector
     * - comparing
     * - Optional handling
     */
    public Map<Genre, Optional<Song>> getMostPopularSongByGenre(List<Song> songs) {
        return songs.stream()
                .collect(Collectors.groupingBy(
                        Song::getPrimaryGenre,
                        Collectors.maxBy(Comparator.comparing(Song::getPopularity))
                ));
    }

    /**
     * Exercise 3: Calculate total duration of a user's playlists.
     * 
     * Uses:
     * - flatMap
     * - map
     * - reduce with identity
     * - method references
     */
    public Duration calculateTotalPlaylistsDuration(List<Playlist> playlists) {
        return playlists.stream()
                .flatMap(playlist -> playlist.getSongs().stream())
                .map(Song::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * Exercise 4: Find users with overlapping favorite genres.
     * 
     * Uses:
     * - nested streams
     * - filter with complex predicate
     * - anyMatch
     * - collect to Map with function results
     */
    public Map<String, List<String>> findUsersWithOverlappingGenres(List<User> users) {
        return users.stream()
                .collect(Collectors.toMap(
                        User::getUsername,
                        user -> users.stream()
                                .filter(otherUser -> !otherUser.equals(user))
                                .filter(otherUser -> user.getFavoriteGenres().stream()
                                        .anyMatch(otherUser.getFavoriteGenres()::contains))
                                .map(User::getUsername)
                                .collect(Collectors.toList())
                ));
    }

    /**
     * Exercise 5: Extract top N songs by play count.
     * 
     * Uses:
     * - sorted
     * - limit
     * - Comparator.comparingInt
     * - method references
     * - reversed() for descending order
     */
    public List<Song> getTopNSongsByPlayCount(List<Song> songs, int n) {
        return songs.stream()
                .sorted(Comparator.comparingInt(Song::getPlayCount).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Exercise 6: Filter albums by complex conditions.
     * 
     * Uses:
     * - filter with complex predicate
     * - combining multiple conditions
     * - method references
     * - stream operations on album songs
     */
    public List<Album> findAlbumsByComplexCriteria(List<Album> albums, Year yearAfter, double minAvgPopularity, Genre genre) {
        return albums.stream()
                .filter(album -> album.getReleaseYear().isAfter(yearAfter))
                .filter(album -> album.getAveragePopularity() >= minAvgPopularity)
                .filter(album -> album.getPrimaryGenre() == genre || 
                                 album.getSongs().stream()
                                      .anyMatch(song -> song.getPrimaryGenre() == genre))
                .collect(Collectors.toList());
    }

    /**
     * Exercise 7: Map albums to DTOs with calculated fields.
     * 
     * Uses:
     * - map
     * - creating new objects in the pipeline
     * - complex attribute calculations using streams
     * - Collectors.joining
     */
    public List<AlbumSummaryDTO> createAlbumSummaries(List<Album> albums) {
        return albums.stream()
                .map(album -> new AlbumSummaryDTO(
                        album.getTitle(),
                        album.getArtist(),
                        album.getReleaseYear(),
                        album.getNumberOfSongs(),
                        album.getSongs().stream().mapToInt(Song::getPlayCount).sum(),
                        album.getSongs().stream().map(Song::getTitle).collect(Collectors.joining(", ")),
                        album.getSongs().stream()
                              .max(Comparator.comparing(Song::getPopularity))
                              .map(Song::getTitle)
                              .orElse("N/A")
                ))
                .collect(Collectors.toList());
    }

    /**
     * Exercise 8: Advanced user statistics combining multiple data sources.
     * 
     * Uses:
     * - Stream pipeline with multiple intermediate operations
     * - Custom collectors
     * - Complex data aggregation
     */
    public Map<String, UserStatisticsDTO> generateUserStatistics(
            List<User> users, 
            List<Song> allSongs, 
            List<Playlist> allPlaylists) {
        
        Map<String, List<Playlist>> userPlaylists = allPlaylists.stream()
                .collect(Collectors.groupingBy(Playlist::getOwnerId));
        
        Map<String, Song> songById = allSongs.stream()
                .collect(Collectors.toMap(Song::getId, Function.identity()));
        
        return users.stream()
                .collect(Collectors.toMap(
                        User::getUsername,
                        user -> {
                            List<Song> userTopSongs = user.getSongPlayCounts().entrySet().stream()
                                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                    .limit(5)
                                    .map(entry -> songById.get(entry.getKey()))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());
                            
                            List<Playlist> userPlaylistsList = userPlaylists.getOrDefault(user.getId(), Collections.emptyList());
                            
                            Set<Genre> mostPlayedGenres = userTopSongs.stream()
                                    .map(Song::getPrimaryGenre)
                                    .collect(Collectors.toSet());
                            
                            return new UserStatisticsDTO(
                                    user.getUsername(),
                                    user.isPremium(),
                                    user.getTotalPlayCount(),
                                    userPlaylistsList.size(),
                                    userTopSongs,
                                    mostPlayedGenres
                            );
                        }
                ));
    }

    /**
     * Exercise 9: Partition users by subscription type with play statistics.
     * 
     * Uses:
     * - partitioningBy
     * - Collectors.mapping as a downstream collector
     * - Collectors.summarizingInt
     * - Processing statistics objects
     */
    public Map<Boolean, IntSummaryStatistics> getPlayStatisticsByPremiumStatus(List<User> users) {
        return users.stream()
                .collect(Collectors.partitioningBy(
                        User::isPremium,
                        Collectors.summarizingInt(User::getTotalPlayCount)
                ));
    }

    /**
     * Exercise 10: Advanced playlist recommendations based on user listening habits.
     * 
     * Uses:
     * - flatMap
     * - Complex collector chains
     * - Weighted score calculations
     * - Custom sorting criteria
     */
    public List<Song> getPersonalizedRecommendations(User user, List<Song> allSongs, List<Album> allAlbums) {
        // Extract user's favorite genres
        Set<Genre> userFavoriteGenres = user.getFavoriteGenres();
        
        // Get IDs of songs the user has already played
        Set<String> playedSongIds = user.getSongPlayCounts().keySet();
        
        // Get artists from user's favorite albums
        Set<String> favoriteArtists = user.getFavoriteAlbums().stream()
                .map(Album::getArtist)
                .collect(Collectors.toSet());
        
        // Get songs from albums of favorite artists that the user hasn't played yet
        List<Song> artistBasedRecommendations = allAlbums.stream()
                .filter(album -> favoriteArtists.contains(album.getArtist()))
                .flatMap(album -> album.getSongs().stream())
                .filter(song -> !playedSongIds.contains(song.getId()))
                .toList();
        
        // Calculate a recommendation score for each song
        return allSongs.stream()
                .filter(song -> !playedSongIds.contains(song.getId()))
                .map(song -> {
                    double genreScore = userFavoriteGenres.contains(song.getPrimaryGenre()) ? 10.0 : 0.0;
                    double secondaryGenreScore = song.getSecondaryGenres().stream()
                            .filter(userFavoriteGenres::contains)
                            .count() * 2.0;
                    double popularityScore = song.getPopularity() * 0.5;
                    double artistBonus = song.getArtists().stream()
                            .anyMatch(favoriteArtists::contains) ? 20.0 : 0.0;
                    
                    double totalScore = genreScore + secondaryGenreScore + popularityScore + artistBonus;
                    return Map.entry(song, totalScore);
                })
                .sorted(Map.Entry.<Song, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * DTO for Album summary information
     */
    public static class AlbumSummaryDTO {
        private final String title;
        private final String artist;
        private final Year releaseYear;
        private final int numberOfSongs;
        private final int totalPlayCount;
        private final String songTitles;
        private final String mostPopularSong;

        public AlbumSummaryDTO(String title, String artist, Year releaseYear, 
                              int numberOfSongs, int totalPlayCount, 
                              String songTitles, String mostPopularSong) {
            this.title = title;
            this.artist = artist;
            this.releaseYear = releaseYear;
            this.numberOfSongs = numberOfSongs;
            this.totalPlayCount = totalPlayCount;
            this.songTitles = songTitles;
            this.mostPopularSong = mostPopularSong;
        }

        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public Year getReleaseYear() { return releaseYear; }
        public int getNumberOfSongs() { return numberOfSongs; }
        public int getTotalPlayCount() { return totalPlayCount; }
        public String getSongTitles() { return songTitles; }
        public String getMostPopularSong() { return mostPopularSong; }

        @Override
        public String toString() {
            return "AlbumSummaryDTO{" +
                    "title='" + title + '\'' +
                    ", artist='" + artist + '\'' +
                    ", releaseYear=" + releaseYear +
                    ", numberOfSongs=" + numberOfSongs +
                    ", totalPlayCount=" + totalPlayCount +
                    ", mostPopularSong='" + mostPopularSong + '\'' +
                    '}';
        }
    }

    /**
     * DTO for User statistics
     */
    public static class UserStatisticsDTO {
        private final String username;
        private final boolean isPremium;
        private final int totalPlayCount;
        private final int numberOfPlaylists;
        private final List<Song> topSongs;
        private final Set<Genre> mostPlayedGenres;

        public UserStatisticsDTO(String username, boolean isPremium, int totalPlayCount,
                                int numberOfPlaylists, List<Song> topSongs,
                                Set<Genre> mostPlayedGenres) {
            this.username = username;
            this.isPremium = isPremium;
            this.totalPlayCount = totalPlayCount;
            this.numberOfPlaylists = numberOfPlaylists;
            this.topSongs = topSongs;
            this.mostPlayedGenres = mostPlayedGenres;
        }

        public String getUsername() { return username; }
        public boolean isPremium() { return isPremium; }
        public int getTotalPlayCount() { return totalPlayCount; }
        public int getNumberOfPlaylists() { return numberOfPlaylists; }
        public List<Song> getTopSongs() { return topSongs; }
        public Set<Genre> getMostPlayedGenres() { return mostPlayedGenres; }

        @Override
        public String toString() {
            return "UserStatisticsDTO{" +
                    "username='" + username + '\'' +
                    ", isPremium=" + isPremium +
                    ", totalPlayCount=" + totalPlayCount +
                    ", numberOfPlaylists=" + numberOfPlaylists +
                    ", topSongs=" + topSongs.size() +
                    ", mostPlayedGenres=" + mostPlayedGenres +
                    '}';
        }
    }
}