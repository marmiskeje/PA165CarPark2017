package cz.muni.fi.pa165.service;

import java.time.Clock;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimeServiceImpl implements TimeService{

    @Override
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now(Clock.systemUTC());
    }

}

