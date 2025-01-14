package sn.ods.starterkit_spring.infrastructure.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping
    public String logMessage(@RequestParam String level, @RequestParam String message) {
        logService.log(level, message);
        return "Message logged: " + message;
    }
}