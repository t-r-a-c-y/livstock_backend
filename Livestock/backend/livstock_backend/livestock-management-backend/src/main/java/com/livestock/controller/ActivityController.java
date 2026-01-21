@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityDto>>> getAllActivities() {
        List<ActivityDto> activities = activityService.getAllActivities();
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityDto>> getActivityById(@PathVariable UUID id) {
        ActivityDto dto = activityService.getActivityById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ActivityDto>> createActivity(
            @RequestBody @Valid ActivityDto dto,          // same DTO
            Authentication authentication) {

        UUID currentUserId = getCurrentUserId(authentication); // your way of getting user
        ActivityDto created = activityService.createActivity(dto, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(created, "Activity created"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ActivityDto>> updateActivity(
            @PathVariable UUID id,
            @RequestBody @Valid ActivityDto dto,
            Authentication authentication) {

        UUID currentUserId = getCurrentUserId(authentication);
        ActivityDto updated = activityService.updateActivity(id, dto, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(updated, "Activity updated"));
    }

    // other endpoints...
}