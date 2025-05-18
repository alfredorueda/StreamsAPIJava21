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
 * MusicAnalyticsService provides advanced data analysis capabilities for a music streaming platform.
 * This service offers analytical functions to better understand user behavior, music trends,
 * and generate personalized recommendations - similar to how Spotify or Apple Music would
 * analyze user data to enhance the listening experience.
 * 
 * The class contains 15 exercises showcasing Java 21 Stream API, lambda expressions,
 * method references, and other modern Java features in practical music domain contexts.
 */
public class MusicAnalyticsService {

    /**
     * Exercise 1: Calculate average popularity ratings for each music genre.
     * 
     * Business Context:
     * This analysis helps the music platform understand which genres are generally rated 
     * higher by users. This data can drive content acquisition decisions, marketing campaigns,
     * or homepage featuring strategies. For example, if Electronic music has high average
     * popularity, the platform might feature more electronic artists in "New Releases" sections.
     * 
     * Functionality:
     * - Takes a list of songs from the catalog
     * - Groups them by primary genre
     * - Calculates the average popularity rating (0-100 scale) for each genre
     * - Returns a map of genres to their average popularity scores
     * 
     * Example:
     * Input: List of songs including Rock songs (avg 75/100) and Pop songs (avg 85/100)
     * Output: {ROCK=75.0, POP=85.0}
     * 
     * Technical Implementation:
     * Uses Stream API's collect operation with groupingBy and averagingDouble collectors
     */
    public Map<Genre, Double> getAveragePopularityByGenre(List<Song> songs) {
        return songs.stream()
                .collect(Collectors.groupingBy(
                        Song::getPrimaryGenre,
                        Collectors.averagingDouble(Song::getPopularity)
                ));
    }

    /**
     * Exercise 2: Find the most popular song in each music genre.
     * 
     * Business Context:
     * This analysis helps identify "hit songs" in each genre for creating genre-based
     * "Top Hits" playlists or recommending representative songs to new users based on 
     * their preferred genres. It enables the platform to showcase the best examples of 
     * each genre when users explore music categories.
     * 
     * Functionality:
     * - Takes a list of songs from the catalog
     * - Groups them by primary genre
     * - Finds the song with the highest popularity rating in each genre
     * - Returns a map of genres to their most popular songs
     * 
     * Example:
     * Input: "Bohemian Rhapsody" (Rock, 98.5), "Stairway to Heaven" (Rock, 97.2)
     * Output: {ROCK=Optional[Bohemian Rhapsody]}
     * 
     * Technical Implementation:
     * Uses Stream API's collect operation with groupingBy and maxBy collectors
     */
    public Map<Genre, Optional<Song>> getMostPopularSongByGenre(List<Song> songs) {
        return songs.stream()
                .collect(Collectors.groupingBy(
                        Song::getPrimaryGenre,
                        Collectors.maxBy(Comparator.comparing(Song::getPopularity))
                ));
    }

    /**
     * Exercise 3: Calculate the total playing time of all songs in a user's playlists.
     * 
     * Business Context:
     * This feature helps users understand their total listening time commitment across 
     * all their playlists. It's useful for listeners planning activities (e.g., "I need 
     * a 30-minute workout mix") or for platform analytics (e.g., analyzing average playlist 
     * durations to optimize offline playback features).
     * 
     * Functionality:
     * - Takes a list of user playlists
     * - Extracts all songs from all playlists
     * - Calculates the sum of all song durations
     * - Returns the total playback time as a Duration object
     * 
     * Example:
     * Input: "Workout" playlist (3 songs, 12 mins total), "Relaxing" playlist (5 songs, 25 mins total)
     * Output: Duration of 37 minutes
     * 
     * Technical Implementation:
     * Uses flatMap to process all songs across playlists and reduce to combine durations
     */
    public Duration calculateTotalPlaylistsDuration(List<Playlist> playlists) {
        return playlists.stream()
                .flatMap(playlist -> playlist.getSongs().stream())
                .map(Song::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * Exercise 4: Find users who share musical taste based on genre preferences.
     * 
     * Business Context:
     * This social discovery feature helps users connect with others who have similar 
     * music tastes. It enables "Listeners like you also follow..." recommendations, 
     * community building, and collaborative playlist creation between users with 
     * overlapping music interests.
     * 
     * Functionality:
     * - Takes a list of platform users
     * - For each user, finds other users who share at least one favorite genre
     * - Returns a map linking each username to a list of other usernames with overlapping tastes
     * 
     * Example:
     * Input: "rockfan" (likes ROCK, METAL), "popgirl" (likes POP, RNB), "diverse_listener" (likes ROCK, JAZZ)
     * Output: {"rockfan": ["diverse_listener"], "diverse_listener": ["rockfan"], "popgirl": []}
     * 
     * Technical Implementation:
     * Uses nested streams with filtering and complex predicate conditions
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
     * Exercise 5: Generate a ranked list of the most played songs across the platform.
     * 
     * Business Context:
     * This analysis powers "Top Charts" features, helping users discover trending content 
     * and allowing the platform to track viral hits. It helps music marketers identify 
     * songs that resonate with listeners and is essential for building "Most Popular" 
     * sections in the app.
     * 
     * Functionality:
     * - Takes a list of songs and a number N
     * - Sorts songs by their play count in descending order
     * - Returns the top N most-played songs
     * 
     * Example:
     * Input: (Songs list, N=3) with "Shape of You" (2.5M plays), "Blinding Lights" (2.2M), "Dance Monkey" (2.1M), "Bad Guy" (1.8M)
     * Output: ["Shape of You", "Blinding Lights", "Dance Monkey"]
     * 
     * Technical Implementation:
     * Uses sorting operations with Comparator, limit, and reversed ordering
     */
    public List<Song> getTopNSongsByPlayCount(List<Song> songs, int n) {
        return songs.stream()
                .sorted(Comparator.comparingInt(Song::getPlayCount).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Exercise 6: Find albums matching multiple complex criteria for content curation.
     * 
     * Business Context:
     * This multi-criteria filtering enables content curators to create specialized 
     * collections like "Recent High-Rated Rock Albums" or "Acclaimed Jazz Releases 
     * from 2020 Onward". It's used to power discovery features, editorial playlists, 
     * and to highlight specific parts of the catalog to target audiences.
     * 
     * Functionality:
     * - Takes a list of albums and filtering criteria (year cutoff, min popularity, genre)
     * - Filters albums that:
     *   1. Were released after the specified year
     *   2. Have an average popularity rating above the minimum threshold
     *   3. Either belong to the specified genre or contain at least one song in that genre
     * - Returns a list of albums matching all criteria
     * 
     * Example:
     * Input: (Albums, year=2018, minRating=80, genre=POP)
     * Output: ["Fine Line" (Pop, 2019, avg rating 85), "Future Nostalgia" (Pop, 2020, avg rating 92)]
     * 
     * Technical Implementation:
     * Uses multiple filter operations with complex predicates and logical combinations
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
     * Exercise 7: Generate rich album summaries for the platform's user interface.
     * 
     * Business Context:
     * This transformation creates comprehensive album summary cards for display in the 
     * app's UI, search results, and recommendations. It consolidates key album information 
     * and statistics into a single object, ready for frontend display - similar to album 
     * detail pages on music streaming services.
     * 
     * Functionality:
     * - Takes a list of albums
     * - For each album:
     *   1. Extracts basic metadata (title, artist, year)
     *   2. Calculates statistics (number of songs, total play count)
     *   3. Generates derived data (comma-separated song list, most popular track)
     * - Returns DTOs containing both the raw and calculated data
     * 
     * Example:
     * Input: Album "Thriller" (Michael Jackson, 1982, 9 songs)
     * Output: AlbumSummaryDTO with title="Thriller", artist="Michael Jackson", year=1982, 
     *         songs=9, popularSong="Billie Jean", etc.
     * 
     * Technical Implementation:
     * Uses map transformations with complex object construction and nested stream operations
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
     * Exercise 8: Generate detailed user profile statistics for personalization.
     * 
     * Business Context:
     * This analysis creates comprehensive user profiles to power personalization features.
     * It's similar to Spotify's "Wrapped" year-end summaries but can be generated at any time.
     * These profiles help understand user preferences for better recommendations and
     * help users discover patterns in their own listening habits.
     * 
     * Functionality:
     * - Takes lists of users, songs, and playlists
     * - For each user:
     *   1. Finds their top 5 most-played songs
     *   2. Counts how many playlists they've created
     *   3. Identifies their most-played music genres
     *   4. Aggregates listening statistics
     * - Returns detailed user statistics DTOs
     * 
     * Example:
     * Input: User "rockfan" with multiple rock song plays and 3 playlists
     * Output: UserStatisticsDTO with username="rockfan", top songs=["Bohemian Rhapsody", ...], 
     *         favourite genres=[ROCK, METAL], playlist count=3
     * 
     * Technical Implementation:
     * Uses complex stream pipelines with multi-source data aggregation and transformation
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
     * Exercise 9: Generate listening statistics comparing premium and free user behavior.
     * 
     * Business Context:
     * This analysis helps business teams understand differences in engagement between 
     * premium and free users. It provides data for subscription strategy, informs 
     * conversion tactics, and helps evaluate the success of premium features. Product 
     * teams use this data to identify which features drive premium engagement.
     * 
     * Functionality:
     * - Takes a list of platform users
     * - Divides users into two groups: premium subscribers and free users
     * - For each group, calculates statistical summaries of listening activity:
     *   * Count of users in each group
     *   * Average play count per user
     *   * Minimum and maximum play counts
     *   * Total play count for each group
     * - Returns statistical summaries for both user groups
     * 
     * Example:
     * Input: 5 users (2 premium, 3 free) with various play counts
     * Output: {true=IntSummaryStatistics{count=2, sum=300, min=100, avg=150.0, max=200}, 
     *          false=IntSummaryStatistics{count=3, sum=150, min=20, avg=50.0, max=80}}
     * 
     * Technical Implementation:
     * Uses partitioningBy collector with summarizingInt downstream collector
     */
    public Map<Boolean, IntSummaryStatistics> getPlayStatisticsByPremiumStatus(List<User> users) {
        return users.stream()
                .collect(Collectors.partitioningBy(
                        User::isPremium,
                        Collectors.summarizingInt(User::getTotalPlayCount)
                ));
    }

    /**
     * Exercise 10: Generate personalized song recommendations based on user preferences.
     * 
     * Business Context:
     * This is the core recommendation algorithm that powers "Recommended for You" sections.
     * It analyzes user behavior, tastes, and explicit preferences to suggest new content
     * they might enjoy but haven't discovered yet. This feature drives approximately 30% of 
     * all streams on major music platforms and is critical for user engagement and retention.
     * 
     * Functionality:
     * - Takes a user profile, available songs, and album catalog
     * - Creates a personalized recommendation score for each unplayed song based on:
     *   1. Match with user's favorite genres (primary and secondary)
     *   2. Song popularity in the wider user base
     *   3. Match with user's favorite artists
     * - Filters out songs the user has already played
     * - Returns the top 10 highest-scoring songs as recommendations
     * 
     * Example:
     * Input: User who likes Rock and Metal, favors Queen, hasn't heard "Stairway to Heaven"
     * Output: List including "Stairway to Heaven" and other rock/metal songs the user hasn't played
     * 
     * Technical Implementation:
     * Uses complex scoring algorithm with filter, map, sort operations and multi-criteria evaluation
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
     * Exercise 11: Analyze music catalog distribution by decade and genre.
     * 
     * Business Context:
     * This analysis gives content strategists a comprehensive view of catalog distribution.
     * It helps identify gaps in the music library (e.g., "we need more 90s Jazz") and
     * informs licensing decisions. It's also useful for creating decade-based features like
     * "80s Rock Classics" or "Discover the 70s" thematic experiences.
     * 
     * Functionality:
     * - Takes a list of songs in the catalog
     * - Groups songs first by decade of release (1970s, 1980s, etc.)
     * - Within each decade, further groups songs by their primary genre
     * - For each group, creates simplified song summaries with key information
     * - Returns a nested map organizing the entire catalog by decade and genre
     * 
     * Example:
     * Input: Songs from various decades and genres
     * Output: {"1980s": {ROCK: [SongSummary("Sweet Child O' Mine",...), ...], POP: [...]}}, 
     *          "1990s": {ALTERNATIVE: [...], HIPHOP: [...]}}
     * 
     * Technical Implementation:
     * Uses multi-level groupingBy collectors with downstream mapping and custom record types
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
     * Exercise 12: Discover artist collaboration networks within the music catalog.
     * 
     * Business Context:
     * This analysis maps the artistic collaboration network in the music industry. It powers
     * features like "Artists often featured together" and "Collaborative discography." 
     * It helps users discover new artists through their connections to familiar ones and enables
     * the platform to identify key industry connectors or suggest potential new collaborations.
     * 
     * Functionality:
     * - Takes a list of songs in the catalog
     * - Identifies songs with multiple artists (collaborations)
     * - For each unique pair of collaborating artists, lists the songs they worked on together
     * - Returns a map where each key is a unique artist pair and the value is their joint songs
     * 
     * Example:
     * Input: Songs including "Don't Call Me Angel" (Ariana Grande, Miley Cyrus, Lana Del Rey)
     * Output: {ArtistPair("Ariana Grande", "Miley Cyrus"): ["Don't Call Me Angel"], 
     *          ArtistPair("Ariana Grande", "Lana Del Rey"): ["Don't Call Me Angel"],
     *          ArtistPair("Miley Cyrus", "Lana Del Rey"): ["Don't Call Me Angel"]}
     * 
     * Technical Implementation:
     * Uses flatMap transformation with combinatorial generation and complex grouping
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
     * Exercise 13: Calculate users' affinity scores for different music genres.
     * 
     * Business Context:
     * This analysis quantifies each user's affinity for different music genres based on
     * their listening patterns, explicit preferences, and playlist curation behavior.
     * These scores power personalized genre exploration features, targeted new release 
     * notifications ("New Rock releases for you"), and help optimize the user's homepage
     * with content from their high-affinity genres.
     * 
     * Functionality:
     * - Takes lists of users, songs, and playlists
     * - For each user, calculates a 0-100 affinity score for each music genre based on:
     *   1. Direct song plays (primary genre gets full weight, secondary genres half weight)
     *   2. Explicitly marked favorite genres (significant boost)
     *   3. Genres represented in user-created playlists
     * - Normalizes scores to a 0-100 scale for consistency
     * - Returns a map of users to their genre affinity scores
     * 
     * Example:
     * Input: User "rockfan" who mostly plays Rock songs but has some Pop and has Rock as favorite
     * Output: {User("rockfan"): {ROCK: 100.0, POP: 35.0, ELECTRONIC: 12.0, ...}}
     * 
     * Technical Implementation:
     * Uses complex multi-source data processing with weighted calculations and custom collectors
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
     * Exercise 14: Generate smart playlists tailored to specific activities and moods.
     * 
     * Business Context:
     * This feature creates activity-specific playlists like "Workout Mix," "Focus Time," or 
     * "Roadtrip Playlist" with precise duration control and content filtering. Unlike simple
     * genre-based playlists, these adapt to user preferences, limit repetition, and balance
     * familiar tracks with discovery - similar to Spotify's Daily Mixes or Apple Music's stations.
     * 
     * Functionality:
     * - Takes a list of available songs and playlist parameters:
     *   1. Preferred genres and artists
     *   2. Year range for song selection
     *   3. Target duration (e.g., 30-minute workout)
     *   4. Variety factor (1-10) controlling selection diversity
     * - Applies complex filtering based on user preferences
     * - Adapts sorting strategy based on the variety factor:
     *   * Low variety: strict genre match, highest popularity first
     *   * Medium variety: balances popularity with recency and some genre flexibility
     *   * High variety: includes more genre exploration and incorporates randomness
     * - Builds a playlist of appropriate duration
     * - Returns a list of songs that matches all criteria
     * 
     * Example:
     * Input: (songs, genres=[POP, ELECTRONIC], artists=["The Weeknd"], years=2010-2023, duration=45min, variety=5)
     * Output: A playlist with ~45 minutes of medium-variety pop and electronic music, favoring but not
     *         limited to The Weeknd, all from 2010-2023
     * 
     * Technical Implementation:
     * Uses predicate combinations, conditional pipeline construction, and dynamic sorting strategies
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
            comparator = Comparator.comparing((Song song) -> preferredGenres.contains(song.getPrimaryGenre()) ? 0 : 1)
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
     * Exercise 15: Analyze track transition patterns to improve shuffle and radio algorithms.
     * 
     * Business Context:
     * This analysis studies how users naturally sequence music to create more human-like
     * automatic playback. Unlike truly random shuffles, this powers "smart shuffle" features
     * that feel more intentional. It improves radio stations' song sequencing by learning
     * which songs flow well after others - similar to how DJs craft seamless sets.
     * 
     * Functionality:
     * - Takes user listening histories and a song lookup map
     * - Analyzes the sequential order of songs in users' listening sessions
     * - For each song, calculates the probability distribution of what songs typically follow it
     * - Returns a transition probability matrix that maps:
     *   Song A → {Song B: 30% likelihood, Song C: 45% likelihood, Song D: 25% likelihood}
     * 
     * Example:
     * Input: User histories showing "Sweet Child O' Mine" is followed by "Paradise City" 8 times
     *        and by "November Rain" 2 times
     * Output: {"Sweet Child O' Mine" → {"Paradise City": 0.8, "November Rain": 0.2}}
     * 
     * Technical Implementation:
     * Uses sequential stream processing with state tracking and probability calculation
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

    // Support records and classes for the exercises
    
    /**
     * Record representing a decade for grouping songs.
     * Automatically normalizes years to decade boundaries (e.g., 1987 → 1980s).
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
     * Record representing a pair of collaborating artists.
     * Ensures consistent ordering for proper equals/hashCode behavior
     * regardless of the order artists are provided.
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
     * Record representing a summary of song information,
     * used for simplified presentations in UI components.
     */
    public record SongSummary(String title, String artists, double popularity, int playCount) {}

    /**
     * DTO for Album summary information displayed in the user interface.
     * Combines raw album data with calculated statistics.
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
     * DTO for User statistics used in profile displays and analytics.
     * Aggregates data from multiple sources to create a comprehensive user profile.
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