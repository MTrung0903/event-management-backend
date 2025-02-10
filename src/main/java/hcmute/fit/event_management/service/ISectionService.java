package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.SectionDTO;
import hcmute.fit.event_management.entity.Section;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;


public interface ISectionService {

    List<SectionDTO> getSectionOfEvent(int eventId);

    SectionDTO getSectionById(int sectionId);

    SectionDTO addSection(MultipartFile handout, SectionDTO sectionDTO) ;

    boolean updateSection(MultipartFile handout, SectionDTO sectionDTO) ;

    boolean deleteSection(int sectionId);
}
