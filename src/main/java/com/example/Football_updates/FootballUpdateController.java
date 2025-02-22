package com.example.Football_updates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class FootballUpdateController {

    @Value("${telex.integration.app_name}")
    private String appName;

    @Value("${telex.integration.app_description}")
    private String appDescription;

    @Value("${telex.integration.app_logo}")
    private String appLogo;

    @Value("${telex.integration.app_url}")
    private String appUrl;

    @Value("${telex.integration.background_color}")
    private String backgroundColor;

    @Value("${telex.integration.is_active}")
    private boolean isActive;

    @Value("${telex.integration.integration_type}")
    private String integrationType;

    @Value("${telex.integration.key_features}")
    private List<String> keyFeatures;

    @Value("${telex.integration.author}")
    private String author;

    @Value("${telex.integration.settings.time_interval}")
    private String timeInterval;

    @Value("${telex.integration.integration_category}")
    private String integration_category;

    @Value("${telex.integration.settings.event_type}")
    private String eventType;

    @Value("${telex.integration.target_url}")
    private String targetUrl;

    @Value("${telex.integration.tick_url}")
    private String tickUrl;

    private final FootballUpdateMonitor footballUpdateMonitor;
    private static final Logger logger = LoggerFactory.getLogger(FootballUpdateController.class);


    public FootballUpdateController(FootballUpdateMonitor footballUpdateMonitor){
        this.footballUpdateMonitor = footballUpdateMonitor;
    }

    // endpoint that returns the integration.json
    @GetMapping("/integration.json")
    public Map<String, Object> getIntegrationJson () {
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> description = new HashMap<>();
        description.put("app_name",appName);
        description.put("app_description", appDescription);
        description.put("app_logo",appLogo);
        description.put("app_url", appUrl);
        description.put("background_color", backgroundColor);

        Map<String, Object> data = new HashMap<>();
        data.put("descriptions", description);
        data.put("integration_type", integrationType);
        data.put("integration_category",integration_category);
        data.put("is_active", isActive);
        data.put("key_features", keyFeatures);
        data.put("author", author);

        // settings for time interval and event type
        Map<String, Object> time = new HashMap<>();
        time.put("label", "time_interval");
        time.put("type", "dropdown");
        time.put("required", true);
        time.put("default", timeInterval);
        time.put("options", new String[]{"one-hour"});

        //event type
        Map<String, Object> event = new HashMap<>();
        event.put("label", "event_type");
        event.put("type", "text");
        event.put("required", true);
        event.put("default", eventType);

        data.put("settings", new Map[]{time,event});
        data.put("target_url", targetUrl);
        data.put("tick_url", tickUrl);

        response.put("data", data);
        return response;
    }

    // tick endpoint, that will post to the telex Api
    @PostMapping("/tick")
    public Mono<ResponseEntity<String>> handleFootballUpdate(@RequestBody MonitorPayload payload) {
        logger.info("Received tick request with payload: {}", payload);

        return Mono.fromRunnable(() -> footballUpdateMonitor.fetchAndSendLiveScores())
                .thenReturn(ResponseEntity.ok("Football Live scores sent to Telex"))
                .onErrorResume(e -> {
                    logger.error("Error while fetching and sending football live scores", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to fetch and send football live scores"));
                });
    }}