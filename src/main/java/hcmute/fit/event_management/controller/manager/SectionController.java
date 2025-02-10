package hcmute.fit.event_management.controller.manager;

import hcmute.fit.event_management.dto.SectionDTO;
import hcmute.fit.event_management.entity.Section;
import hcmute.fit.event_management.service.ISectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import payload.Response;

import java.util.List;

@RestController
@RequestMapping("/man/section")
public class SectionController {

    @Autowired
    private ISectionService sectionService;



    @GetMapping("/{sectionId}")
    public ResponseEntity<?> findSectionById(@PathVariable int sectionId) {
        SectionDTO section = sectionService.getSectionById(sectionId);
        Response response = new Response();
        response.setData(section);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("")
    public ResponseEntity<?> addSection(@RequestParam("file") MultipartFile handout, @ModelAttribute SectionDTO sectionDTO) {
        Response response = new Response();
        response.setData(sectionService.addSection(handout,sectionDTO));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<?> updateSection(@RequestParam("file") MultipartFile handout, @ModelAttribute SectionDTO sectionDTO){
        Response response = new Response();
        response.setData(sectionService.updateSection(handout, sectionDTO));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<?> deleteSection(@PathVariable int sectionId){
        Response response = new Response();
        response.setData(sectionService.deleteSection(sectionId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
