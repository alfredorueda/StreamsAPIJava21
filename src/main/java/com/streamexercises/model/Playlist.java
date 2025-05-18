package com.streamexercises.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a playlist in the music streaming service
 */
public class Playlist {
    private final String id;
    private String name;
    private final String ownerId;
    private final LocalDateTime creationDate;
    private final List<Song> songs;
    private boolean isPublic;
    private String description;

    public Playlist(String name, String ownerId, boolean isPublic, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.ownerId = ownerId;
        this.creationDate = LocalDateTime.now();
        this.songs = new ArrayList<>();
        this.isPublic = isPublic;
        this.description = description;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Methods
    public void addSong(Song song) {
        if (song != null && !songs.contains(song)) {
            songs.add(song);
        }
    }

    public boolean removeSong(Song song) {
        return songs.remove(song);
    }

    public void removeSongAt(int index) {
        if (index >= 0 && index < songs.size()) {
            songs.remove(index);
        }
    }

    public int getNumberOfSongs() {
        return songs.size();
    }

    public Duration getTotalDuration() {
        return songs.stream()
                .map(Song::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public Set<Genre> getAllGenres() {
        return songs.stream()
                .flatMap(song -> song.getAllGenres().stream())
                .collect(Collectors.toSet());
    }

    public Optional<Genre> getMostFrequentGenre() {
        return songs.stream()
                .map(Song::getPrimaryGenre)
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public double getAveragePopularity() {
        return songs.stream()
                .mapToDouble(Song::getPopularity)
                .average()
                .orElse(0.0);
    }

    public void shuffle() {
        Collections.shuffle(songs);
    }

    public void sortByTitle() {
        songs.sort(Comparator.comparing(Song::getTitle));
    }

    public void sortByPopularity() {
        songs.sort(Comparator.comparing(Song::getPopularity).reversed());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(id, playlist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "name='" + name + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", creationDate=" + creationDate +
                ", numberOfSongs=" + songs.size() +
                ", isPublic=" + isPublic +
                '}';
    }
}