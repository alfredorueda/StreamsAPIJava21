package com.streamexercises.model;

import java.time.Year;
import java.util.*;

/**
 * Represents a music album in the streaming service
 */
public class Album {
    private final String id;
    private final String title;
    private final String artist;
    private final Year releaseYear;
    private final List<Song> songs;
    private final Genre primaryGenre;
    private final boolean isCompilation;

    public Album(String title, String artist, Year releaseYear, List<Song> songs, Genre primaryGenre, boolean isCompilation) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.artist = artist;
        this.releaseYear = releaseYear;
        this.songs = songs != null ? new ArrayList<>(songs) : new ArrayList<>();
        this.primaryGenre = primaryGenre;
        this.isCompilation = isCompilation;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Year getReleaseYear() {
        return releaseYear;
    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);
    }

    public Genre getPrimaryGenre() {
        return primaryGenre;
    }

    public boolean isCompilation() {
        return isCompilation;
    }

    // Methods
    public void addSong(Song song) {
        if (song != null && !songs.contains(song)) {
            songs.add(song);
        }
    }

    public int getNumberOfSongs() {
        return songs.size();
    }

    public double getAveragePopularity() {
        return songs.stream()
                .mapToDouble(Song::getPopularity)
                .average()
                .orElse(0.0);
    }

    public int getTotalPlayCount() {
        return songs.stream()
                .mapToInt(Song::getPlayCount)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(id, album.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Album{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", releaseYear=" + releaseYear +
                ", numberOfSongs=" + songs.size() +
                ", primaryGenre=" + primaryGenre +
                '}';
    }
}