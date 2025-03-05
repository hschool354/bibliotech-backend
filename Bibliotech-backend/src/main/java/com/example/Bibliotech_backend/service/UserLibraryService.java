package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.model.UserLibrary;
import com.example.Bibliotech_backend.repository.UserLibraryRepository;
import com.example.Bibliotech_backend.model.UserLibraryId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLibraryService {

    @Autowired
    private UserLibraryRepository userLibraryRepository;

    public List<UserLibrary> getUserLibrary(Integer userId) {
        return userLibraryRepository.findByUserId(userId);
    }

    public UserLibrary addBookToLibrary(Integer userId, Integer bookId) {
        UserLibrary userLibrary = new UserLibrary();
        userLibrary.setUserId(userId);
        userLibrary.setBookId(bookId);
        userLibrary.setStatus(UserLibrary.Status.NEXT_UP);
        userLibrary.setProgressPercentage(0);
        userLibrary.setReadingStreak(0);
        userLibrary.setTotalReadingTime(0);
        return userLibraryRepository.save(userLibrary);
    }

    public UserLibrary updateBookStatus(Integer userId, Integer bookId, UserLibrary updatedLibrary) {
        UserLibrary userLibrary = userLibraryRepository.findById(new UserLibraryId(userId, bookId)).orElse(null);
        if (userLibrary != null) {
            userLibrary.setStatus(updatedLibrary.getStatus());
            userLibrary.setProgressPercentage(updatedLibrary.getProgressPercentage());
            userLibrary.setLastReadDate(updatedLibrary.getLastReadDate());
            userLibrary.setReadingStreak(updatedLibrary.getReadingStreak());
            userLibrary.setLastStreakDate(updatedLibrary.getLastStreakDate());
            userLibrary.setTotalReadingTime(updatedLibrary.getTotalReadingTime());
            userLibrary.setBookmarks(updatedLibrary.getBookmarks());
            userLibrary.setNotes(updatedLibrary.getNotes());
            return userLibraryRepository.save(userLibrary);
        }
        return null;
    }
}