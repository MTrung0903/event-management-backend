package hcmute.fit.event_management.controller.manager;


import hcmute.fit.event_management.service.Impl.ZoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import payload.Response;

import java.util.Map;

@RestController
@RequestMapping("/api/zoom")
public class ZoomController {

}