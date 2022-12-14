package org.jiranibora.com.meetings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jiranibora.com.application.Utility;
import org.jiranibora.com.models.Meeting;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final Utility utility;

    public MeetingService(MeetingRepository meetingRepository, Utility utility) {
        this.meetingRepository = meetingRepository;
        this.utility = utility;
    }

    public Boolean startNewMeeting() {
        // prevent a meeting start if there is already one active
        // Find the meeting first

        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct", "Nov","Dec"};
        Meeting meeting = Meeting
                .builder()
                .meetingDate(LocalDateTime.now())
                .month(months[LocalDateTime.now().getMonthValue()-1])
                .meetingId("MEET" + utility.randomApplicationID().substring(6))
                .status("ON")
                .build();
        try {
            meetingRepository.saveAndFlush(meeting);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Internal Server Error");

        }

        return true;

    }

    // End the meeting
    public Boolean stopMeeting(String meetingId) throws Exception{
        Optional<Meeting> meeting = meetingRepository.findById(meetingId);

        if(meeting.isEmpty()){
            return false;
        }
        meeting.get().setStatus("ENDED");

        try{
           meetingRepository.saveAndFlush(meeting.get());
        }
        catch(Exception e){
          throw new Exception("Something went wrong" + e.getMessage());
        }
      return true;
    
    }
    public List<Meeting> getAllMeetings(){
        return meetingRepository.findAll();
    }
}
