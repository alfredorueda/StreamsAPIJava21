package com.streamexercises.service;

import com.streamexercises.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MusicAnalyticsService that contains 15 tests for advanced Stream API exercises.
 * Each test includes comprehensive data setup and assertions to validate the expected results.
 */
public class MusicAnalyticsServiceTest {

    private MusicAnalyticsService service;
    private List<Song> allSongs;
    private List<Album> allAlbums;
    private List<User> allUsers;
    private List<Playlist> allPlaylists;

    @BeforeEach
    void setUp() {
        service = new MusicAnalyticsService();
        initializeTestData();
    }

    @Test
    @DisplayName("Exercise 1: Group songs by genre and find average popularity for each genre")
    void testGetAveragePopularityByGenre() {
        // Execute the method under test
        Map<Genre, Double> result = service.getAveragePopularityByGenre(allSongs);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.containsKey(Genre.ROCK));
        assertTrue(result.containsKey(Genre.POP));

        // Validate the average popularity calculations
        Map<Genre, List<Double>> popularityByGenre = allSongs.stream()
                .collect(Collectors.groupingBy(
                        Song::getPrimaryGenre,
                        Collectors.mapping(Song::getPopularity, Collectors.toList())
                ));

        for (Map.Entry<Genre, Double> entry : result.entrySet()) {
            Genre genre = entry.getKey();
            Double calculatedAvg = entry.getValue();
            
            List<Double> popularities = popularityByGenre.get(genre);
            double expectedAvg = popularities.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            assertEquals(expectedAvg, calculatedAvg, 0.001, 
                    "Average popularity for genre " + genre + " should be " + expectedAvg);
        }
    }

    @Test
    @DisplayName("Exercise 2: Find the most popular song for each genre")
    void testGetMostPopularSongByGenre() {
        // Execute the method under test
        Map<Genre, Optional<Song>> result = service.getMostPopularSongByGenre(allSongs);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.containsKey(Genre.POP));
        
        // Check that the returned songs are indeed the most popular for each genre
        for (Map.Entry<Genre, Optional<Song>> entry : result.entrySet()) {
            Genre genre = entry.getKey();
            Optional<Song> mostPopularSong = entry.getValue();
            
            // The song should be present
            assertTrue(mostPopularSong.isPresent(), "Most popular song for genre " + genre + " should be present");

            // It should be the most popular song for that genre
            Song expected = allSongs.stream()
                    .filter(song -> song.getPrimaryGenre() == genre)
                    .max(Comparator.comparing(Song::getPopularity))
                    .orElseThrow();
            
            assertEquals(expected, mostPopularSong.get(),
                    "Most popular song for genre " + genre + " is incorrect");
        }
    }

    @Test
    @DisplayName("Exercise 3: Calculate total duration of a user's playlists")
    void testCalculateTotalPlaylistsDuration() {
        // Get playlists for a specific user
        User testUser = allUsers.get(0);
        List<Playlist> userPlaylists = allPlaylists.stream()
                .filter(playlist -> playlist.getOwnerId().equals(testUser.getId()))
                .collect(Collectors.toList());
        
        // Execute the method under test
        Duration result = service.calculateTotalPlaylistsDuration(userPlaylists);

        // Calculate the expected duration
        Duration expected = Duration.ZERO;
        for (Playlist playlist : userPlaylists) {
            for (Song song : playlist.getSongs()) {
                expected = expected.plus(song.getDuration());
            }
        }

        // Verify the result
        assertEquals(expected, result, "Total duration calculation is incorrect");
        
        // Additional test with empty playlists
        assertEquals(Duration.ZERO, service.calculateTotalPlaylistsDuration(Collections.emptyList()),
                "Duration for empty playlists should be zero");
    }

    @Test
    @DisplayName("Exercise 4: Find users with overlapping favorite genres")
    void testFindUsersWithOverlappingGenres() {
        // Execute the method under test
        Map<String, List<String>> result = service.findUsersWithOverlappingGenres(allUsers);

        // Verify the result
        assertNotNull(result);
        assertEquals(allUsers.size(), result.size(), "Result should contain all users");
        
        // Manually calculate overlap and verify
        for (User user : allUsers) {
            List<String> expectedMatches = new ArrayList<>();
            Set<Genre> userGenres = user.getFavoriteGenres();
            
            for (User otherUser : allUsers) {
                if (!otherUser.equals(user)) {
                    Set<Genre> otherUserGenres = otherUser.getFavoriteGenres();
                    boolean hasOverlap = userGenres.stream()
                            .anyMatch(otherUserGenres::contains);
                    
                    if (hasOverlap) {
                        expectedMatches.add(otherUser.getUsername());
                    }
                }
            }
            
            List<String> actualMatches = result.get(user.getUsername());
            assertEquals(expectedMatches.size(), actualMatches.size(),
                    "Number of overlapping users for " + user.getUsername() + " should be " + expectedMatches.size());
            
            assertTrue(expectedMatches.containsAll(actualMatches) && actualMatches.containsAll(expectedMatches),
                    "User overlaps for " + user.getUsername() + " don't match expected");
        }
    }

    @Test
    @DisplayName("Exercise 5: Extract top N songs by play count")
    void testGetTopNSongsByPlayCount() {
        int n = 5;
        
        // Execute the method under test
        List<Song> result = service.getTopNSongsByPlayCount(allSongs, n);

        // Verify the result
        assertNotNull(result);
        assertEquals(n, result.size(), "Should return exactly " + n + " songs");
        
        // Check sorting is correct
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getPlayCount() >= result.get(i + 1).getPlayCount(),
                    "Songs should be sorted by play count in descending order");
        }
        
        // Verify these are indeed the top N songs
        List<Song> expectedTopSongs = allSongs.stream()
                .sorted(Comparator.comparingInt(Song::getPlayCount).reversed())
                .limit(n)
                .collect(Collectors.toList());
        
        assertEquals(expectedTopSongs, result, "The returned songs should be the top " + n + " by play count");
    }

    @Test
    @DisplayName("Exercise 6: Filter albums by complex conditions")
    void testFindAlbumsByComplexCriteria() {
        Year yearAfter = Year.of(2010);
        double minAvgPopularity = 75.0;
        Genre genre = Genre.POP;
        
        // Execute the method under test
        List<Album> result = service.findAlbumsByComplexCriteria(allAlbums, yearAfter, minAvgPopularity, genre);

        // Verify the result
        assertNotNull(result);
        
        // Verify all returned albums match the criteria
        for (Album album : result) {
            assertTrue(album.getReleaseYear().isAfter(yearAfter),
                    "Album should be released after " + yearAfter);
            
            assertTrue(album.getAveragePopularity() >= minAvgPopularity,
                    "Album average popularity should be >= " + minAvgPopularity);
            
            boolean hasMatchingGenre = album.getPrimaryGenre() == genre || 
                    album.getSongs().stream().anyMatch(song -> song.getPrimaryGenre() == genre);
            
            assertTrue(hasMatchingGenre, "Album should have matching genre criteria");
        }
        
        // Verify all albums that should match are included
        List<Album> expectedAlbums = allAlbums.stream()
                .filter(album -> album.getReleaseYear().isAfter(yearAfter))
                .filter(album -> album.getAveragePopularity() >= minAvgPopularity)
                .filter(album -> album.getPrimaryGenre() == genre || 
                             album.getSongs().stream().anyMatch(song -> song.getPrimaryGenre() == genre))
                .collect(Collectors.toList());
        
        assertEquals(expectedAlbums.size(), result.size(), "Should return all matching albums");
        assertTrue(expectedAlbums.containsAll(result) && result.containsAll(expectedAlbums),
                "The returned albums should match the expected filtered set");
    }

    @Test
    @DisplayName("Exercise 7: Map albums to DTOs with calculated fields")
    void testCreateAlbumSummaries() {
        // Execute the method under test
        List<MusicAnalyticsService.AlbumSummaryDTO> result = service.createAlbumSummaries(allAlbums);

        // Verify the result
        assertNotNull(result);
        assertEquals(allAlbums.size(), result.size(), "Should create DTOs for all albums");

        // Verify DTO contents for a specific album
        Album testAlbum = allAlbums.get(0);
        MusicAnalyticsService.AlbumSummaryDTO dto = result.stream()
                .filter(summary -> summary.getTitle().equals(testAlbum.getTitle()))
                .findFirst()
                .orElseThrow();
        
        assertEquals(testAlbum.getTitle(), dto.getTitle());
        assertEquals(testAlbum.getArtist(), dto.getArtist());
        assertEquals(testAlbum.getReleaseYear(), dto.getReleaseYear());
        assertEquals(testAlbum.getNumberOfSongs(), dto.getNumberOfSongs());
        
        // Verify calculated fields
        int expectedTotalPlayCount = testAlbum.getSongs().stream()
                .mapToInt(Song::getPlayCount)
                .sum();
        assertEquals(expectedTotalPlayCount, dto.getTotalPlayCount(), "Total play count should match");
        
        String expectedMostPopularSong = testAlbum.getSongs().stream()
                .max(Comparator.comparing(Song::getPopularity))
                .map(Song::getTitle)
                .orElse("N/A");
        assertEquals(expectedMostPopularSong, dto.getMostPopularSong(), "Most popular song should match");
    }

    @Test
    @DisplayName("Exercise 8: Advanced user statistics combining multiple data sources")
    void testGenerateUserStatistics() {
        // Execute the method under test
        Map<String, MusicAnalyticsService.UserStatisticsDTO> result = 
                service.generateUserStatistics(allUsers, allSongs, allPlaylists);

        // Verify the result
        assertNotNull(result);
        assertEquals(allUsers.size(), result.size(), "Should generate statistics for all users");

        // Verify details for a specific user
        User testUser = allUsers.get(0);
        MusicAnalyticsService.UserStatisticsDTO userStats = result.get(testUser.getUsername());
        
        assertNotNull(userStats);
        assertEquals(testUser.getUsername(), userStats.getUsername());
        assertEquals(testUser.isPremium(), userStats.isPremium());
        assertEquals(testUser.getTotalPlayCount(), userStats.getTotalPlayCount());
        
        // Verify playlist count
        long expectedPlaylistCount = allPlaylists.stream()
                .filter(playlist -> playlist.getOwnerId().equals(testUser.getId()))
                .count();
        assertEquals(expectedPlaylistCount, userStats.getNumberOfPlaylists(), "Playlist count should match");
        
        // Verify top songs are the most played ones
        Map<String, Song> songById = allSongs.stream()
                .collect(Collectors.toMap(Song::getId, song -> song));
        
        List<Song> expectedTopSongs = testUser.getSongPlayCounts().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> songById.get(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        assertEquals(expectedTopSongs.size(), userStats.getTopSongs().size(), 
                "Should have expected number of top songs");
        
        for (int i = 0; i < Math.min(expectedTopSongs.size(), userStats.getTopSongs().size()); i++) {
            assertEquals(expectedTopSongs.get(i).getId(), userStats.getTopSongs().get(i).getId(),
                    "Top song at position " + i + " should match");
        }
    }

    @Test
    @DisplayName("Exercise 9: Partition users by subscription type with play statistics")
    void testGetPlayStatisticsByPremiumStatus() {
        // Execute the method under test
        Map<Boolean, IntSummaryStatistics> result = service.getPlayStatisticsByPremiumStatus(allUsers);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.containsKey(true), "Should contain statistics for premium users");
        assertTrue(result.containsKey(false), "Should contain statistics for non-premium users");

        // Calculate expected statistics
        Map<Boolean, List<User>> usersByPremium = allUsers.stream()
                .collect(Collectors.partitioningBy(User::isPremium));
        
        IntSummaryStatistics expectedPremiumStats = usersByPremium.get(true).stream()
                .mapToInt(User::getTotalPlayCount)
                .summaryStatistics();
        
        IntSummaryStatistics expectedNonPremiumStats = usersByPremium.get(false).stream()
                .mapToInt(User::getTotalPlayCount)
                .summaryStatistics();

        // Verify statistics for premium users
        IntSummaryStatistics actualPremiumStats = result.get(true);
        assertEquals(expectedPremiumStats.getCount(), actualPremiumStats.getCount(), "Premium count should match");
        assertEquals(expectedPremiumStats.getSum(), actualPremiumStats.getSum(), "Premium sum should match");
        assertEquals(expectedPremiumStats.getAverage(), actualPremiumStats.getAverage(), 0.001, "Premium average should match");
        assertEquals(expectedPremiumStats.getMax(), actualPremiumStats.getMax(), "Premium max should match");
        assertEquals(expectedPremiumStats.getMin(), actualPremiumStats.getMin(), "Premium min should match");

        // Verify statistics for non-premium users
        IntSummaryStatistics actualNonPremiumStats = result.get(false);
        assertEquals(expectedNonPremiumStats.getCount(), actualNonPremiumStats.getCount(), "Non-premium count should match");
        assertEquals(expectedNonPremiumStats.getSum(), actualNonPremiumStats.getSum(), "Non-premium sum should match");
        assertEquals(expectedNonPremiumStats.getAverage(), actualNonPremiumStats.getAverage(), 0.001, "Non-premium average should match");
        assertEquals(expectedNonPremiumStats.getMax(), actualNonPremiumStats.getMax(), "Non-premium max should match");
        assertEquals(expectedNonPremiumStats.getMin(), actualNonPremiumStats.getMin(), "Non-premium min should match");
    }

    @Test
    @DisplayName("Exercise 10: Advanced personalized recommendations based on user listening habits")
    void testGetPersonalizedRecommendations() {
        // Select a user for testing
        User testUser = allUsers.get(0);
        
        // Make sure the user has some played songs, favorite genres and albums
        if (testUser.getSongPlayCounts().isEmpty()) {
            testUser.playSong(allSongs.get(0).getId(), 10);
        }
        
        if (testUser.getFavoriteGenres().isEmpty()) {
            testUser.addFavoriteGenre(Genre.ROCK);
            testUser.addFavoriteGenre(Genre.POP);
        }
        
        if (testUser.getFavoriteAlbums().isEmpty()) {
            testUser.addFavoriteAlbum(allAlbums.get(0));
        }

        // Execute the method under test
        List<Song> recommendations = service.getPersonalizedRecommendations(testUser, allSongs, allAlbums);

        // Verify the result
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty(), "Should return some recommendations");
        assertTrue(recommendations.size() <= 10, "Should return no more than 10 recommendations");

        // Verify none of the recommended songs have been played by the user
        Set<String> playedSongIds = testUser.getSongPlayCounts().keySet();
        for (Song song : recommendations) {
            assertFalse(playedSongIds.contains(song.getId()), 
                    "Recommendations should not include already played songs");
        }

        // Verify recommendations are ordered by relevance score
        Set<Genre> userFavoriteGenres = testUser.getFavoriteGenres();
        Set<String> favoriteArtists = testUser.getFavoriteAlbums().stream()
                .map(Album::getArtist)
                .collect(Collectors.toSet());
        
        // Compare some pairs to check ordering
        if (recommendations.size() >= 2) {
            Song firstSong = recommendations.get(0);
            Song secondSong = recommendations.get(1);
            
            double score1 = calculateRecommendationScore(firstSong, userFavoriteGenres, favoriteArtists);
            double score2 = calculateRecommendationScore(secondSong, userFavoriteGenres, favoriteArtists);
            
            assertTrue(score1 >= score2, "Recommendations should be ordered by relevance score");
        }
    }

    @Test
    @DisplayName("Exercise 11: Analyze library by decade and genre")
    void testAnalyzeLibraryByDecadeAndGenre() {
        // Act
        Map<MusicAnalyticsService.Decade, Map<Genre, List<MusicAnalyticsService.SongSummary>>> result = 
            service.analyzeLibraryByDecadeAndGenre(allSongs);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty(), "Should group songs into at least one decade");
        
        // Check that a decade has song data
        MusicAnalyticsService.Decade someDecade = result.keySet().iterator().next();
        assertNotNull(someDecade);
        
        // Verify decade formatting
        assertTrue(someDecade.toString().endsWith("s"), 
                "Decade should be formatted with 's' at the end, e.g. '2020s'");
        
        // Verify the structure for one decade
        Map<Genre, List<MusicAnalyticsService.SongSummary>> genreMap = result.get(someDecade);
        assertNotNull(genreMap);
        assertFalse(genreMap.isEmpty(), "Should have at least one genre per decade");
        
        // Verify that a genre has songs
        Genre someGenre = genreMap.keySet().iterator().next();
        List<MusicAnalyticsService.SongSummary> songSummaries = genreMap.get(someGenre);
        assertNotNull(songSummaries);
        assertFalse(songSummaries.isEmpty(), "Should have at least one song per genre");
        
        // Verify SongSummary record structure
        MusicAnalyticsService.SongSummary summary = songSummaries.get(0);
        assertNotNull(summary.title());
        assertNotNull(summary.artists());
        assertTrue(summary.popularity() >= 0 && summary.popularity() <= 100);
        assertTrue(summary.playCount() >= 0);
    }

    @Test
    @DisplayName("Exercise 12: Find artist collaborations")
    void testFindArtistCollaborations() {
        // Act
        Map<MusicAnalyticsService.ArtistPair, List<Song>> result = service.findArtistCollaborations(allSongs);
        
        // Assert
        assertNotNull(result);
        
        // Check if collaborations were found (there might not be any in the test data)
        if (!result.isEmpty()) {
            // Get a collaboration pair
            Map.Entry<MusicAnalyticsService.ArtistPair, List<Song>> entry = 
                result.entrySet().iterator().next();
            
            MusicAnalyticsService.ArtistPair pair = entry.getKey();
            assertNotNull(pair.artist1());
            assertNotNull(pair.artist2());
            assertNotEquals(pair.artist1(), pair.artist2(), 
                    "Artists in a pair should be different");
            
            // Verify that artist1 is lexicographically before artist2 (canonicalization)
            assertTrue(pair.artist1().compareTo(pair.artist2()) <= 0,
                    "ArtistPair should ensure consistent ordering (artist1 <= artist2)");
            
            // Verify songs list
            List<Song> collaborationSongs = entry.getValue();
            assertFalse(collaborationSongs.isEmpty());
            
            // Verify each song actually contains both artists
            Song song = collaborationSongs.get(0);
            assertTrue(song.getArtists().contains(pair.artist1()));
            assertTrue(song.getArtists().contains(pair.artist2()));
        }
    }

    @Test
    @DisplayName("Exercise 13: Calculate genre affinity scores")
    void testCalculateGenreAffinityScores() {
        // Act
        Map<User, Map<Genre, Double>> result = 
            service.calculateGenreAffinityScores(allUsers, allSongs, allPlaylists);
        
        // Assert
        assertNotNull(result);
        assertEquals(allUsers.size(), result.size(), "Should have scores for all users");
        
        // Check a user's scores
        User someUser = allUsers.get(0);
        Map<Genre, Double> userScores = result.get(someUser);
        assertNotNull(userScores);
        
        // Verify scores are properly normalized
        double maxScore = userScores.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
                
        assertTrue(maxScore <= 100.0, "Maximum score should not exceed 100");
        
        // Verify that favorite genres have higher scores
        if (!someUser.getFavoriteGenres().isEmpty()) {
            Genre favoriteGenre = someUser.getFavoriteGenres().iterator().next();
            
            if (userScores.containsKey(favoriteGenre)) {
                double favoriteGenreScore = userScores.get(favoriteGenre);
                
                // The favorite genre's score should be relatively high
                // This is a heuristic check, might need adjustment based on implementation
                assertTrue(favoriteGenreScore >= 20.0, 
                        "Favorite genres should have higher affinity scores");
            }
        }
    }

    @Test
    @DisplayName("Exercise 14: Generate dynamic playlist")
    void testGenerateDynamicPlaylist() {
        // Arrange
        Set<Genre> preferredGenres = Set.of(Genre.POP, Genre.ELECTRONIC);
        Set<String> preferredArtists = new HashSet<>();
        if (!allSongs.isEmpty()) {
            preferredArtists.add(allSongs.get(0).getArtists().iterator().next());
        }
        Year earliestYear = Year.of(2000);
        Year latestYear = Year.of(2023);
        Duration targetDuration = Duration.ofMinutes(20);
        int varietyFactor = 5;
        
        // Act
        List<Song> result = service.generateDynamicPlaylist(
            allSongs,
            preferredGenres, 
            preferredArtists,
            earliestYear,
            latestYear,
            targetDuration,
            varietyFactor
        );
        
        // Assert
        assertNotNull(result);
        
        if (!result.isEmpty()) {
            // Check that playlist doesn't exceed target duration by too much
            Duration totalDuration = result.stream()
                    .map(Song::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);
                    
            assertTrue(totalDuration.compareTo(targetDuration) <= 0 || 
                      result.size() == 1, 
                    "Playlist should not exceed target duration unless it contains only one song");
            
            // Check year range constraints
            assertTrue(result.stream()
                    .allMatch(song -> !song.getReleaseYear().isBefore(earliestYear) && 
                                      !song.getReleaseYear().isAfter(latestYear)),
                    "All songs should be within the specified year range");
                    
            // With variety factor 5, should prefer preferred genres but not exclusively
            long genreMatchCount = result.stream()
                    .filter(song -> preferredGenres.contains(song.getPrimaryGenre()))
                    .count();
                    
            // At medium variety, at least some songs should match preferred genres
            if (preferredGenres != null && !preferredGenres.isEmpty() && result.size() > 1) {
                assertTrue(genreMatchCount > 0, 
                        "At least some songs should match preferred genres at medium variety");
            }
        }
    }

    @Test
    @DisplayName("Exercise 15: Analyze track transition probabilities")
    void testAnalyzeTrackTransitionProbabilities() {
        // Setup users with listening history
        allUsers.forEach(user -> {
            // Create a simple listening sequence for testing
            if (allSongs.size() >= 3) {
                List<String> history = new ArrayList<>();
                history.add(allSongs.get(0).getId());
                history.add(allSongs.get(1).getId());
                history.add(allSongs.get(0).getId());
                history.add(allSongs.get(2).getId());
                user.setListeningHistory(history);
            }
        });
        
        // Act
        Map<Song, Map<Song, Double>> result = 
            service.analyzeTrackTransitionProbabilities(allUsers, allSongs.stream().collect(Collectors.toMap(Song::getId, song -> song)));
        
        // Assert
        assertNotNull(result);
        
        if (!result.isEmpty() && allSongs.size() >= 3) {
            // Get transition probabilities for the first song
            Song firstSong = allSongs.get(0);
            Map<Song, Double> transitions = result.get(firstSong);
            assertNotNull(transitions);
            
            // Verify probabilities sum to approximately 1.0
            double sum = transitions.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();
            assertEquals(1.0, sum, 0.001, 
                    "Transition probabilities for a song should sum to 1.0");
            
            // For our test data, first song transitions to songs[1] and songs[2]
            Song secondSong = allSongs.get(1);
            Song thirdSong = allSongs.get(2);
            
            if (transitions.containsKey(secondSong) && transitions.containsKey(thirdSong)) {
                double probToSecond = transitions.get(secondSong);
                double probToThird = transitions.get(thirdSong);
                
                assertTrue(probToSecond > 0, "There should be a positive probability to transition to second song");
                assertTrue(probToThird > 0, "There should be a positive probability to transition to third song");
                
                // Based on our mock data (0→1, 0→2), these should be equal
                assertEquals(probToSecond, probToThird, 0.001, 
                        "With equal transitions, probabilities should be equal");
            }
        }
    }

    /**
     * Helper method to calculate recommendation score for validation
     */
    private double calculateRecommendationScore(Song song, Set<Genre> userFavoriteGenres, Set<String> favoriteArtists) {
        double genreScore = userFavoriteGenres.contains(song.getPrimaryGenre()) ? 10.0 : 0.0;
        
        double secondaryGenreScore = song.getSecondaryGenres().stream()
                .filter(userFavoriteGenres::contains)
                .count() * 2.0;
        
        double popularityScore = song.getPopularity() * 0.5;
        
        double artistBonus = song.getArtists().stream()
                .anyMatch(favoriteArtists::contains) ? 20.0 : 0.0;
        
        return genreScore + secondaryGenreScore + popularityScore + artistBonus;
    }

    /**
     * Initialize comprehensive test data for all exercises
     */
    private void initializeTestData() {
        createSongs();
        createAlbums();
        createUsers();
        createPlaylists();
    }

    /**
     * Create a diverse collection of songs with various genres, popularity, and play counts
     */
    private void createSongs() {
        allSongs = new ArrayList<>();

        // Create songs with different genres, durations, and popularity
        
        // ROCK songs
        addSong("Bohemian Rhapsody", Set.of("Queen"), Duration.ofMinutes(5).plusSeconds(55), Year.of(1975), 
                Genre.ROCK, Set.of(Genre.CLASSICAL), 1500000, 98.5);
        
        addSong("Stairway to Heaven", Set.of("Led Zeppelin"), Duration.ofMinutes(8).plusSeconds(2), Year.of(1971), 
                Genre.ROCK, Set.of(), 1200000, 97.2);
        
        addSong("Sweet Child O' Mine", Set.of("Guns N' Roses"), Duration.ofMinutes(5).plusSeconds(56), Year.of(1987), 
                Genre.ROCK, Set.of(Genre.METAL), 980000, 94.8);
                
        addSong("Nothing Else Matters", Set.of("Metallica"), Duration.ofMinutes(6).plusSeconds(28), Year.of(1991), 
                Genre.ROCK, Set.of(Genre.METAL), 890000, 91.6);
                
        // POP songs
        addSong("Billie Jean", Set.of("Michael Jackson"), Duration.ofMinutes(4).plusSeconds(54), Year.of(1982), 
                Genre.POP, Set.of(Genre.RNB), 2000000, 99.1);
        
        addSong("Shape of You", Set.of("Ed Sheeran"), Duration.ofMinutes(3).plusSeconds(53), Year.of(2017), 
                Genre.POP, Set.of(), 2500000, 97.8);
        
        addSong("Bad Guy", Set.of("Billie Eilish"), Duration.ofMinutes(3).plusSeconds(14), Year.of(2019), 
                Genre.POP, Set.of(Genre.ELECTRONIC), 1800000, 93.4);
                
        addSong("Shake It Off", Set.of("Taylor Swift"), Duration.ofMinutes(3).plusSeconds(39), Year.of(2014), 
                Genre.POP, Set.of(), 1700000, 92.5);
                
        // HIP_HOP songs
        addSong("Lose Yourself", Set.of("Eminem"), Duration.ofMinutes(5).plusSeconds(26), Year.of(2002), 
                Genre.HIP_HOP, Set.of(), 1600000, 96.3);
        
        addSong("Sicko Mode", Set.of("Travis Scott", "Drake"), Duration.ofMinutes(5).plusSeconds(12), Year.of(2018), 
                Genre.HIP_HOP, Set.of(Genre.ELECTRONIC), 1400000, 92.9);
                
        // ELECTRONIC songs
        addSong("Strobe", Set.of("deadmau5"), Duration.ofMinutes(10).plusSeconds(33), Year.of(2009), 
                Genre.ELECTRONIC, Set.of(), 950000, 90.7);
        
        addSong("Levels", Set.of("Avicii"), Duration.ofMinutes(3).plusSeconds(19), Year.of(2011), 
                Genre.ELECTRONIC, Set.of(Genre.POP), 1300000, 91.2);
                
        // JAZZ songs
        addSong("Take Five", Set.of("Dave Brubeck"), Duration.ofMinutes(5).plusSeconds(24), Year.of(1959), 
                Genre.JAZZ, Set.of(), 700000, 88.5);
        
        addSong("So What", Set.of("Miles Davis"), Duration.ofMinutes(9).plusSeconds(22), Year.of(1959), 
                Genre.JAZZ, Set.of(), 650000, 89.1);
                
        // CLASSICAL songs
        addSong("Moonlight Sonata", Set.of("Ludwig van Beethoven"), Duration.ofMinutes(15).plusSeconds(0), Year.of(1801), 
                Genre.CLASSICAL, Set.of(), 850000, 92.3);
        
        addSong("The Four Seasons - Spring", Set.of("Antonio Vivaldi"), Duration.ofMinutes(10).plusSeconds(31), Year.of(1725), 
                Genre.CLASSICAL, Set.of(), 780000, 90.8);
                
        // More recent songs
        addSong("Blinding Lights", Set.of("The Weeknd"), Duration.ofMinutes(3).plusSeconds(20), Year.of(2020), 
                Genre.POP, Set.of(Genre.ELECTRONIC), 2200000, 96.7);
                
        addSong("Dance Monkey", Set.of("Tones and I"), Duration.ofMinutes(3).plusSeconds(29), Year.of(2019), 
                Genre.POP, Set.of(), 2100000, 95.3);
                
        addSong("As It Was", Set.of("Harry Styles"), Duration.ofMinutes(2).plusSeconds(47), Year.of(2022), 
                Genre.POP, Set.of(Genre.INDIE), 1900000, 94.1);
                
        addSong("Heat Waves", Set.of("Glass Animals"), Duration.ofMinutes(3).plusSeconds(58), Year.of(2020), 
                Genre.INDIE, Set.of(Genre.POP), 1750000, 93.6);
    }

    /**
     * Helper method to create and add a song to the collection
     */
    private void addSong(String title, Set<String> artists, Duration duration, Year releaseYear,
                        Genre primaryGenre, Set<Genre> secondaryGenres, int playCount, double popularity) {
        Song song = new Song(title, artists, duration, releaseYear, primaryGenre, secondaryGenres, playCount, popularity);
        allSongs.add(song);
    }
    
    /**
     * Create albums containing the songs
     */
    private void createAlbums() {
        allAlbums = new ArrayList<>();
        
        // Create albums with different artists and genres
        Album queenAlbum = new Album("A Night at the Opera", "Queen", Year.of(1975),
                List.of(allSongs.get(0)), Genre.ROCK, false);
        
        Album ledZeppelin = new Album("Led Zeppelin IV", "Led Zeppelin", Year.of(1971),
                List.of(allSongs.get(1)), Genre.ROCK, false);
        
        Album gunsNRoses = new Album("Appetite for Destruction", "Guns N' Roses", Year.of(1987),
                List.of(allSongs.get(2)), Genre.ROCK, false);
        
        Album metallica = new Album("Metallica (Black Album)", "Metallica", Year.of(1991),
                List.of(allSongs.get(3)), Genre.ROCK, false);
        
        Album michaelJackson = new Album("Thriller", "Michael Jackson", Year.of(1982),
                List.of(allSongs.get(4)), Genre.POP, false);
        
        Album edSheeran = new Album("÷ (Divide)", "Ed Sheeran", Year.of(2017),
                List.of(allSongs.get(5)), Genre.POP, false);
        
        Album billieEilish = new Album("WHEN WE ALL FALL ASLEEP, WHERE DO WE GO?", "Billie Eilish", Year.of(2019),
                List.of(allSongs.get(6)), Genre.POP, false);
        
        Album taylorSwift = new Album("1989", "Taylor Swift", Year.of(2014),
                List.of(allSongs.get(7)), Genre.POP, false);
        
        Album eminem = new Album("8 Mile Soundtrack", "Various Artists", Year.of(2002),
                List.of(allSongs.get(8)), Genre.HIP_HOP, true);
        
        Album travisScott = new Album("ASTROWORLD", "Travis Scott", Year.of(2018),
                List.of(allSongs.get(9)), Genre.HIP_HOP, false);
        
        Album deadmau5 = new Album("For Lack of a Better Name", "deadmau5", Year.of(2009),
                List.of(allSongs.get(10)), Genre.ELECTRONIC, false);
        
        Album avicii = new Album("True", "Avicii", Year.of(2013),
                List.of(allSongs.get(11)), Genre.ELECTRONIC, false);
        
        Album modernClassics = new Album("Modern Classics", "Various Artists", Year.of(2020),
                List.of(allSongs.get(12), allSongs.get(13), allSongs.get(14), allSongs.get(15)), 
                Genre.CLASSICAL, true);
        
        Album hits2020 = new Album("Hits of 2020", "Various Artists", Year.of(2020),
                List.of(allSongs.get(16), allSongs.get(19)), Genre.POP, true);
        
        Album hits2019 = new Album("Hits of 2019", "Various Artists", Year.of(2019),
                List.of(allSongs.get(6), allSongs.get(17)), Genre.POP, true);
        
        Album harryStyles = new Album("Harry's House", "Harry Styles", Year.of(2022),
                List.of(allSongs.get(18)), Genre.POP, false);
        
        // Add all albums to the collection
        allAlbums.addAll(List.of(
                queenAlbum, ledZeppelin, gunsNRoses, metallica, michaelJackson, edSheeran, 
                billieEilish, taylorSwift, eminem, travisScott, deadmau5, avicii, 
                modernClassics, hits2020, hits2019, harryStyles
        ));
    }
    
    /**
     * Create users with different preferences and listening histories
     */
    private void createUsers() {
        allUsers = new ArrayList<>();
        
        // User 1 - Rock fan
        User rockFan = new User("rockfan123", "rockfan@example.com", LocalDate.of(2018, 3, 15), 
                Set.of(Genre.ROCK, Genre.METAL), "United States", true);
        
        rockFan.addFavoriteAlbum(allAlbums.get(0)); // Queen album
        rockFan.addFavoriteAlbum(allAlbums.get(1)); // Led Zeppelin album
        
        rockFan.playSong(allSongs.get(0).getId(), 150); // Bohemian Rhapsody
        rockFan.playSong(allSongs.get(1).getId(), 120); // Stairway to Heaven
        rockFan.playSong(allSongs.get(2).getId(), 85);  // Sweet Child O Mine
        rockFan.playSong(allSongs.get(3).getId(), 75);  // Nothing Else Matters
        
        // User 2 - Pop fan
        User popFan = new User("popgirl", "popgirl@example.com", LocalDate.of(2019, 7, 22),
                Set.of(Genre.POP, Genre.ELECTRONIC), "Canada", true);
        
        popFan.addFavoriteAlbum(allAlbums.get(5)); // Ed Sheeran album
        popFan.addFavoriteAlbum(allAlbums.get(6)); // Billie Eilish album
        
        popFan.playSong(allSongs.get(4).getId(), 110); // Billie Jean
        popFan.playSong(allSongs.get(5).getId(), 180); // Shape of You
        popFan.playSong(allSongs.get(6).getId(), 165); // Bad Guy
        popFan.playSong(allSongs.get(7).getId(), 145); // Shake It Off
        popFan.playSong(allSongs.get(16).getId(), 190); // Blinding Lights
        
        // User 3 - Diverse taste
        User diverseListener = new User("musiclover", "diverse@example.com", LocalDate.of(2020, 1, 5),
                Set.of(Genre.JAZZ, Genre.CLASSICAL, Genre.POP), "United Kingdom", false);
        
        diverseListener.addFavoriteAlbum(allAlbums.get(12)); // Modern Classics
        diverseListener.addFavoriteAlbum(allAlbums.get(4));  // Thriller
        
        diverseListener.playSong(allSongs.get(4).getId(), 95);  // Billie Jean
        diverseListener.playSong(allSongs.get(12).getId(), 85); // Take Five
        diverseListener.playSong(allSongs.get(13).getId(), 75); // So What
        diverseListener.playSong(allSongs.get(14).getId(), 110); // Moonlight Sonata
        diverseListener.playSong(allSongs.get(15).getId(), 90);  // Four Seasons
        
        // User 4 - Hip hop enthusiast
        User hipHopFan = new User("rapgod", "hiphop@example.com", LocalDate.of(2017, 9, 18),
                Set.of(Genre.HIP_HOP, Genre.RNB), "United States", false);
        
        hipHopFan.addFavoriteAlbum(allAlbums.get(8));  // Eminem album
        hipHopFan.addFavoriteAlbum(allAlbums.get(9));  // Travis Scott album
        
        hipHopFan.playSong(allSongs.get(8).getId(), 200);  // Lose Yourself
        hipHopFan.playSong(allSongs.get(9).getId(), 170);  // Sicko Mode
        hipHopFan.playSong(allSongs.get(4).getId(), 65);   // Billie Jean (some pop)
        
        // User 5 - New user with few plays
        User newUser = new User("newbie2023", "newuser@example.com", LocalDate.of(2023, 1, 2),
                Set.of(Genre.INDIE, Genre.POP), "Australia", false);
        
        newUser.playSong(allSongs.get(18).getId(), 25);  // As It Was
        newUser.playSong(allSongs.get(19).getId(), 20);  // Heat Waves
        
        // Add all users to the collection
        allUsers.addAll(List.of(rockFan, popFan, diverseListener, hipHopFan, newUser));
    }
    
    /**
     * Create playlists for users
     */
    private void createPlaylists() {
        allPlaylists = new ArrayList<>();
        
        // User 1 playlists - Rock fan
        User rockFan = allUsers.get(0);
        Playlist rockClassics = new Playlist("Rock Classics", rockFan.getId(), true, "The best rock songs of all time");
        rockClassics.addSong(allSongs.get(0)); // Bohemian Rhapsody
        rockClassics.addSong(allSongs.get(1)); // Stairway to Heaven
        rockClassics.addSong(allSongs.get(2)); // Sweet Child O Mine
        
        Playlist metalFavorites = new Playlist("Metal Favorites", rockFan.getId(), false, "My favorite metal songs");
        metalFavorites.addSong(allSongs.get(3)); // Nothing Else Matters
        
        // User 2 playlists - Pop fan
        User popFan = allUsers.get(1);
        Playlist popHits = new Playlist("Pop Hits", popFan.getId(), true, "Greatest pop songs");
        popHits.addSong(allSongs.get(4)); // Billie Jean
        popHits.addSong(allSongs.get(5)); // Shape of You
        popHits.addSong(allSongs.get(7)); // Shake It Off
        
        Playlist modernPop = new Playlist("Modern Pop", popFan.getId(), true, "Recent pop hits");
        modernPop.addSong(allSongs.get(6));  // Bad Guy
        modernPop.addSong(allSongs.get(16)); // Blinding Lights
        modernPop.addSong(allSongs.get(17)); // Dance Monkey
        modernPop.addSong(allSongs.get(18)); // As It Was
        
        // User 3 playlists - Diverse listener
        User diverseListener = allUsers.get(2);
        Playlist relaxing = new Playlist("Relaxing Classics", diverseListener.getId(), true, "Relaxing classical music");
        relaxing.addSong(allSongs.get(14)); // Moonlight Sonata
        relaxing.addSong(allSongs.get(15)); // Four Seasons
        
        Playlist jazzNights = new Playlist("Jazz Nights", diverseListener.getId(), true, "Perfect jazz for evenings");
        jazzNights.addSong(allSongs.get(12)); // Take Five
        jazzNights.addSong(allSongs.get(13)); // So What
        
        Playlist eclectic = new Playlist("Eclectic Mix", diverseListener.getId(), false, "My varied taste in music");
        eclectic.addSong(allSongs.get(4));  // Billie Jean
        eclectic.addSong(allSongs.get(12)); // Take Five
        eclectic.addSong(allSongs.get(14)); // Moonlight Sonata
        eclectic.addSong(allSongs.get(18)); // As It Was
        
        // User 4 playlists - Hip hop enthusiast
        User hipHopFan = allUsers.get(3);
        Playlist hipHopEssentials = new Playlist("Hip Hop Essentials", hipHopFan.getId(), true, "Essential hip hop tracks");
        hipHopEssentials.addSong(allSongs.get(8));  // Lose Yourself
        hipHopEssentials.addSong(allSongs.get(9));  // Sicko Mode
        
        // User 5 playlists - New user
        User newUser = allUsers.get(4);
        Playlist discovering = new Playlist("Discovering Music", newUser.getId(), false, "Songs I'm exploring");
        discovering.addSong(allSongs.get(18)); // As It Was
        discovering.addSong(allSongs.get(19)); // Heat Waves
        
        // Add all playlists to the collection
        allPlaylists.addAll(List.of(
                rockClassics, metalFavorites, popHits, modernPop, 
                relaxing, jazzNights, eclectic, hipHopEssentials, discovering
        ));
    }
}