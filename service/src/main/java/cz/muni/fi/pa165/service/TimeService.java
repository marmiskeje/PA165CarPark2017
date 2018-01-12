package cz.muni.fi.pa165.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface TimeService {
    /**
     * Get current time
     * @return current time in UTC
     */
    public LocalDateTime getCurrentTime();
}
