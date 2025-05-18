package com.streamexercises.service;

import com.streamexercises.model.*;

import java.time.Duration;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Service class containing 15 advanced exercises for mastering Java 21 Streams API,
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
                .collect(Collectors.toList());
        
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
     * Exercise 11: Analyze song distribution by decade and genre.
     * 
     * Uses:
     * - Map transformation with custom keys
     * - Complex multi-level grouping
     * - Custom record for return type
     * - Method references and constructor references
     */
    public Map<Decade, Map<Genre, List<SongSummary>>> analyzeLibraryByDecadeAndGenre(List<Song> songs) {
        return songs.stream()
                .collect(Collectors.groupingBy(
                        song -> new Decade(song.getReleaseYear().getValue()),
                        Collectors.groupingBy(
                                Song::getPrimaryGenre,
                                Collectors.mapping(
                                        song -> new SongSummary(
                                                song.getTitle(),
                                                String.join(", ", song.getArtists()),
                                                song.getPopularity(),
                                                song.getPlayCount()
                                        ),
                                        Collectors.toList()
                                )
                        )
                ));
    }

    /**
     * Exercise 12: Find collaboration patterns between artists.
     * 
     * Uses:
     * - flatMap with complex transformation
     * - Collectors.teeing for parallel aggregation
     * - Custom data structure handling
     * - Functional composition
     */
    public Map<ArtistPair, List<Song>> findArtistCollaborations(List<Song> songs) {
        return songs.stream()
                .filter(song -> song.getArtists().size() > 1)
                .flatMap(song -> {
                    List<String> artists = new ArrayList<>(song.getArtists());
                    return IntStream.range(0, artists.size() - 1)
                            .boxed()
                            .flatMap(i -> IntStream.range(i + 1, artists.size())
                                    .mapToObj(j -> Map.entry(
                                            new ArtistPair(artists.get(i), artists.get(j)),
                                            song
                                    ))
                            );
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    /**
     * Exercise 13: Create personalized genre affinity scores for users.
     * 
     * Uses:
     * - Complex weighted calculations
     * - Collectors.toMap with merging function
     * - Multi-step transformations
     * - Optional handling and defaults
     */
    public Map<User, Map<Genre, Double>> calculateGenreAffinityScores(
            List<User> users, 
            List<Song> allSongs,
            List<Playlist> allPlaylists) {
        
        // Build a map of song IDs to songs for quick lookup
        Map<String, Song> songLookup = allSongs.stream()
                .collect(Collectors.toMap(Song::getId, Function.identity()));
        
        // Build map of user IDs to their playlists
        Map<String, List<Playlist>> userPlaylists = allPlaylists.stream()
                .collect(Collectors.groupingBy(Playlist::getOwnerId));
        
        return users.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        user -> {
                            // Calculate base scores from direct plays
                            Map<Genre, Double> baseScores = user.getSongPlayCounts().entrySet().stream()
                                    .filter(entry -> songLookup.containsKey(entry.getKey()))
                                    .map(entry -> {
                                        Song song = songLookup.get(entry.getKey());
                                        int playCount = entry.getValue();
                                        
                                        // Primary genre gets full weight
                                        Map<Genre, Double> scores = new HashMap<>();
                                        scores.put(song.getPrimaryGenre(), playCount * 1.0);
                                        
                                        // Secondary genres get half weight
                                        song.getSecondaryGenres().forEach(genre -> 
                                            scores.put(genre, scores.getOrDefault(genre, 0.0) + playCount * 0.5)
                                        );
                                        
                                        return scores;
                                    })
                                    .flatMap(map -> map.entrySet().stream())
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            Map.Entry::getValue,
                                            Double::sum
                                    ));
                            
                            // Add scores from favorite genres (explicit preferences)
                            user.getFavoriteGenres().forEach(genre ->
                                    baseScores.put(genre, baseScores.getOrDefault(genre, 0.0) + 20.0)
                            );
                            
                            // Add scores from playlist curation
                            Optional.ofNullable(userPlaylists.get(user.getId()))
                                    .orElse(List.of())
                                    .stream()
                                    .flatMap(playlist -> playlist.getSongs().stream())
                                    .collect(Collectors.groupingBy(
                                            Song::getPrimaryGenre,
                                            Collectors.counting()
                                    ))
                                    .forEach((genre, count) ->
                                            baseScores.put(genre, baseScores.getOrDefault(genre, 0.0) + count * 0.5)
                                    );
                            
                            // Normalize scores (0-100 scale)
                            double maxScore = baseScores.values().stream()
                                    .mapToDouble(Double::doubleValue)
                                    .max()
                                    .orElse(1.0);
                            
                            return baseScores.entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            entry -> Math.min(100.0, (entry.getValue() / maxScore) * 100.0)
                                    ));
                        }
                ));
    }

    /**
     * Exercise 14: Create dynamic playlist generator with advanced filtering.
     * 
     * Uses:
     * - Stream pipeline conditionals
     * - Function composition
     * - Predicate factories and combinators
     * - Complex sorting with multiple criteria
     */
    public List<Song> generateDynamicPlaylist(
            List<Song> availableSongs,
            Set<Genre> preferredGenres,
            Set<String> preferredArtists,
            Year earliestYear,
            Year latestYear,
            Duration targetDuration,
            int varietyFactor) {
        
        // Predicate for genre matching with variety factor
        Predicate<Song> genrePredicate = song -> {
            if (preferredGenres == null || preferredGenres.isEmpty()) return true;
            if (preferredGenres.contains(song.getPrimaryGenre())) return true;
            return varietyFactor > 3 && song.getSecondaryGenres().stream()
                    .anyMatch(preferredGenres::contains);
        };
        
        // Predicate for artist matching with variety factor
        Predicate<Song> artistPredicate = song -> {
            if (preferredArtists == null || preferredArtists.isEmpty()) return true;
            boolean hasMatch = song.getArtists().stream().anyMatch(preferredArtists::contains);
            return hasMatch || varietyFactor > 7;
        };
        
        // Predicate for year range
        Predicate<Song> yearPredicate = song -> {
            if (earliestYear == null && latestYear == null) return true;
            if (earliestYear == null) return !song.getReleaseYear().isAfter(latestYear);
            if (latestYear == null) return !song.getReleaseYear().isBefore(earliestYear);
            return !song.getReleaseYear().isBefore(earliestYear) && 
                   !song.getReleaseYear().isAfter(latestYear);
        };
        
        // Combined filter
        Predicate<Song> combinedFilter = genrePredicate.and(artistPredicate).and(yearPredicate);
        
        // Create comparator based on variety factor
        Comparator<Song> comparator;
        if (varietyFactor <= 3) {
            // Low variety: strict match on preferred genres and popularity
            comparator = Comparator.comparing( (Song song) -> preferredGenres.contains(song.getPrimaryGenre()) ? 0 : 1)
                        .thenComparing(Comparator.comparing(Song::getPopularity).reversed());
        } else if (varietyFactor <= 7) {
            // Medium variety: balance genre match with popularity and recency
            comparator = Comparator.comparing(Song::getPopularity)
                        .thenComparing(Song::getReleaseYear).reversed();
        } else {
            // High variety: prioritize diversity in selection
            Random random = new Random();
            comparator = Comparator.comparing(s -> random.nextDouble());
        }
        
        List<Song> candidateSongs = availableSongs.stream()
                .filter(combinedFilter)
                .sorted(comparator)
                .collect(Collectors.toList());
        
        // Build playlist up to target duration
        List<Song> playlist = new ArrayList<>();
        Duration currentDuration = Duration.ZERO;
        
        for (Song song : candidateSongs) {
            if (currentDuration.plus(song.getDuration()).compareTo(targetDuration) > 0 &&
                !playlist.isEmpty()) {
                break;
            }
            playlist.add(song);
            currentDuration = currentDuration.plus(song.getDuration());
        }
        
        return playlist;
    }

    /**
     * Exercise 15: Analyze listening patterns and create track transition probabilities.
     * 
     * Uses:
     * - Sequential stream processing with state
     * - Complex statistical analysis
     * - Custom collector implementation
     * - Multi-level data transformation
     */
    public Map<Song, Map<Song, Double>> analyzeTrackTransitionProbabilities(List<User> users, Map<String, Song> songLookup) {
        // First, collect all user listening sequences
        List<List<Song>> listeningSequences = users.stream()
                .map(User::getId)
                .filter(userId -> {
                    // Find a user by ID and check if they have a listening history
                    Optional<User> user = users.stream()
                            .filter(u -> u.getId().equals(userId))
                            .findFirst();
                    return user.isPresent() && user.get().getListeningHistory() != null 
                           && !user.get().getListeningHistory().isEmpty();
                })
                .map(userId -> {
                    // Get the user's listening history and convert song IDs to Song objects
                    List<String> history = users.stream()
                            .filter(u -> u.getId().equals(userId))
                            .findFirst()
                            .map(User::getListeningHistory)
                            .orElse(List.of());
                            
                    return history.stream()
                            .map(songLookup::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                })
                .filter(sequence -> sequence.size() >= 2)
                .collect(Collectors.toList());
        
        // Create map to count transitions
        Map<Song, Map<Song, Integer>> transitionCounts = new HashMap<>();
        
        // Process each sequence to count transitions
        listeningSequences.forEach(sequence -> {
            for (int i = 0; i < sequence.size() - 1; i++) {
                Song current = sequence.get(i);
                Song next = sequence.get(i + 1);
                
                transitionCounts.computeIfAbsent(current, k -> new HashMap<>())
                               .merge(next, 1, Integer::sum);
            }
        });
        
        // Convert counts to probabilities
        return transitionCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Map<Song, Integer> nextSongCounts = entry.getValue();
                            int total = nextSongCounts.values().stream()
                                    .mapToInt(Integer::intValue)
                                    .sum();
                            
                            return nextSongCounts.entrySet().stream()
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            e -> (double) e.getValue() / total
                                    ));
                        }
                ));
    }

    /**
     * Record representing a decade for grouping songs
     */
    public record Decade(int startYear) {
        public Decade {
            // Normalize to decade start year (e.g., 1980, 1990, etc.)
            startYear = (startYear / 10) * 10;
        }
        
        @Override
        public String toString() {
            return startYear + "s";
        }
    }
    
    /**
     * Record representing a pair of collaborating artists
     */
    public record ArtistPair(String artist1, String artist2) {
        public ArtistPair {
            // Ensure consistent ordering for proper equals/hashCode
            if (artist1.compareTo(artist2) > 0) {
                String temp = artist1;
                artist1 = artist2;
                artist2 = temp;
            }
        }
    }
    
    /**
     * Record representing a summary of song information
     */
    public record SongSummary(String title, String artists, double popularity, int playCount) {}

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