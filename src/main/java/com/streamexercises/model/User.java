package com.streamexercises.model;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents a user of the music streaming service
 */
public class User {
    private final String id;
    private final String username;
    private final String email;
    private final LocalDate joinDate;
    private final Set<Genre> favoriteGenres;
    private final Map<String, Integer> songPlayCounts; // Maps songId to play count
    private final Set<Album> favoriteAlbums;
    private final String country;
    private boolean isPremium;

    public User(String username, String email, LocalDate joinDate, Set<Genre> favoriteGenres, 
                String country, boolean isPremium) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.joinDate = joinDate;
        this.favoriteGenres = favoriteGenres != null ? new HashSet<>(favoriteGenres) : new HashSet<>();
        this.songPlayCounts = new HashMap<>();
        this.favoriteAlbums = new HashSet<>();
        this.country = country;
        this.isPremium = isPremium;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public Set<Genre> getFavoriteGenres() {
        return Collections.unmodifiableSet(favoriteGenres);
    }

    public Map<String, Integer> getSongPlayCounts() {
        return Collections.unmodifiableMap(songPlayCounts);
    }

    public Set<Album> getFavoriteAlbums() {
        return Collections.unmodifiableSet(favoriteAlbums);
    }

    public String getCountry() {
        return country;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    // Methods
    public void addFavoriteGenre(Genre genre) {
        if (genre != null) {
            favoriteGenres.add(genre);
        }
    }

    public void removeFavoriteGenre(Genre genre) {
        favoriteGenres.remove(genre);
    }

    public void playSong(String songId) {
        songPlayCounts.merge(songId, 1, Integer::sum);
    }

    public void playSong(String songId, int count) {
        if (count > 0) {
            songPlayCounts.merge(songId, count, Integer::sum);
        }
    }

    public void addFavoriteAlbum(Album album) {
        if (album != null) {
            favoriteAlbums.add(album);
        }
    }

    public void removeFavoriteAlbum(Album album) {
        favoriteAlbums.remove(album);
    }

    public int getTotalPlayCount() {
        return songPlayCounts.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public Optional<String> getMostPlayedSongId() {
        return songPlayCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", joinDate=" + joinDate +
                ", country='" + country + '\'' +
                ", isPremium=" + isPremium +
                '}';
    }
}