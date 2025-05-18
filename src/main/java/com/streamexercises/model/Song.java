package com.streamexercises.model;

import java.time.Duration;
import java.time.Year;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a song in the music streaming service
 */
public class Song {
    private final String id;
    private final String title;
    private final Set<String> artists;
    private final Duration duration;
    private final Year releaseYear;
    private final Genre primaryGenre;
    private final Set<Genre> secondaryGenres;
    private int playCount;
    private double popularity; // 0.0 to 100.0

    public Song(String title, Set<String> artists, Duration duration, Year releaseYear, 
                Genre primaryGenre, Set<Genre> secondaryGenres, int playCount, double popularity) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.artists = artists != null ? new HashSet<>(artists) : new HashSet<>();
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.primaryGenre = primaryGenre;
        this.secondaryGenres = secondaryGenres != null ? new HashSet<>(secondaryGenres) : new HashSet<>();
        this.playCount = playCount;
        this.popularity = Math.max(0.0, Math.min(100.0, popularity));
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Set<String> getArtists() {
        return new HashSet<>(artists);
    }

    public Duration getDuration() {
        return duration;
    }

    public Year getReleaseYear() {
        return releaseYear;
    }

    public Genre getPrimaryGenre() {
        return primaryGenre;
    }

    public Set<Genre> getSecondaryGenres() {
        return new HashSet<>(secondaryGenres);
    }

    public int getPlayCount() {
        return playCount;
    }

    public double getPopularity() {
        return popularity;
    }

    // Methods
    public void incrementPlayCount() {
        playCount++;
    }

    public void incrementPlayCount(int count) {
        if (count > 0) {
            playCount += count;
        }
    }

    public void updatePopularity(double newPopularity) {
        this.popularity = Math.max(0.0, Math.min(100.0, newPopularity));
    }

    public boolean hasArtist(String artist) {
        return artists.contains(artist);
    }

    public Set<Genre> getAllGenres() {
        Set<Genre> allGenres = new HashSet<>(secondaryGenres);
        allGenres.add(primaryGenre);
        return allGenres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(id, song.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", artists=" + String.join(", ", artists) +
                ", duration=" + duration.toMinutes() + ":" + duration.toSecondsPart() +
                ", releaseYear=" + releaseYear +
                ", primaryGenre=" + primaryGenre +
                '}';
    }
}