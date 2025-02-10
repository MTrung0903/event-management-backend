package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.McDTO;
import hcmute.fit.event_management.entity.Mc;
import hcmute.fit.event_management.service.IFileService;
import hcmute.fit.event_management.service.IMcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payload.Response;

import java.util.List;


@RestController
@RequestMapping("man/mc")
public class McController {
    @Autowired
    private IMcService mcService;
    @Autowired
    private IFileService fileService;

    @GetMapping("")
    public ResponseEntity<?> getAllMC() {
        List<McDTO> list = mcService.getListMc();
        Response response = new Response();
        response.setData(list);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{mcId}")
    public ResponseEntity<?> findMC(@PathVariable int mcId) {
        McDTO mc = mcService.findMcById(mcId);
        Response response = new Response();
        response.setData(mc);
        return ResponseEntity.ok(response);
    }
    @PostMapping("")
    public ResponseEntity<?> addMc(@RequestParam("imageMc") MultipartFile imageMc,
                                   @ModelAttribute McDTO mcDto){
        Response response = new Response();
        response.setData(mcService.addMc(imageMc,mcDto));
        return ResponseEntity.ok(response);
    }

    @PutMapping("")
    public ResponseEntity<?> updateMc(@RequestParam("imageMc") MultipartFile imageMc,
                                      @ModelAttribute McDTO mcDto){
        Response response = new Response();
        response.setData(mcService.updateMc(imageMc,mcDto));
        return ResponseEntity.ok(response);
    }

}
