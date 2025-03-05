package com.example.Bibliotech_backend.service;

import com.example.Bibliotech_backend.model.ReadingHistory;
import com.example.Bibliotech_backend.repository.ReadingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ReadingHistoryService {
    private final ReadingHistoryRepository readingHistoryRepository;

    @Autowired
    public ReadingHistoryService(ReadingHistoryRepository readingHistoryRepository) {
        this.readingHistoryRepository = readingHistoryRepository;
    }

    public List<ReadingHistory> getUserReadingHistory(Integer userId) {
        return readingHistoryRepository.findByUserIdOrderBySessionStartDesc(userId);
    }

    public ReadingHistory saveReadingHistory(ReadingHistory readingHistory) {
        if (readingHistory.getSessionEnd().before(readingHistory.getSessionStart())) {
            throw new IllegalArgumentException("Session end time cannot be before start time");
        }
        return readingHistoryRepository.save(readingHistory);
    }

    public ReadingHistory recordReadingSession(ReadingHistory readingSession) {
        // Validate session times
        if (readingSession.getSessionStart() == null) {
            readingSession.setSessionStart(new Timestamp(System.currentTimeMillis()));
        }

        // If no end time provided, set it to current time
        if (readingSession.getSessionEnd() == null) {
            readingSession.setSessionEnd(new Timestamp(System.currentTimeMillis()));
        }

        // Ensure end time is not before start time
        if (readingSession.getSessionEnd().before(readingSession.getSessionStart())) {
            throw new IllegalArgumentException("Session end time cannot be before start time");
        }

        // Calculate reading duration if not provided
        if (readingSession.getReadingDuration() == null) {
            long durationMillis = readingSession.getSessionEnd().getTime() - readingSession.getSessionStart().getTime();
            readingSession.setReadingDuration((int) (durationMillis / 1000)); // Convert to seconds
        }

        // Save the reading session
        return readingHistoryRepository.save(readingSession);
    }
}