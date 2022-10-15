package org.jiranibora.com.meetings;

import java.util.LinkedHashMap;
import java.util.List;

import org.jiranibora.com.models.Meeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/meeting")
public class MeetingController {
    private final MeetingService meetingService;
    private final LinkedHashMap<String, String> meetingMap = new LinkedHashMap<>();

    @Autowired
    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping("/start/{month}")
    public ResponseEntity<?> startMeeting(@PathVariable(required = true) String month) {
        Boolean result = meetingService.startNewMeeting(month);

        if (result) {
            meetingMap.put("code", "200");
            meetingMap.put("message", "Your meeting was initiated successfully");
        } else {
            meetingMap.put("code", "409");
            meetingMap.put("message", "There is an existing meeting, please end it before starting a new one");
        }

        return ResponseEntity.status(Integer.valueOf(meetingMap.get("code"))).body(meetingMap);
    }

    @PutMapping("/stop")
    public ResponseEntity<?> endMeeting(@RequestParam String meeting_id) throws Exception {

        Boolean stopResult = meetingService.stopMeeting(meeting_id);

        if (stopResult) {
            meetingMap.put("code", "200");
            meetingMap.put("message", "The meeting with ID " + meeting_id + " was closed successfully");
        } else {
            meetingMap.put("code", "404");
            meetingMap.put("message", "No such meeting exists");
        }

        return ResponseEntity.status(Integer.valueOf(meetingMap.get("code"))).body(meetingMap);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMeetings() {
        List<Meeting> meetings = meetingService.getAllMeetings();
        return ResponseEntity.status(200).body(meetings);

    }
}
