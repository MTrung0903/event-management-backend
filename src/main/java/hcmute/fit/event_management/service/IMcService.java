package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.McDTO;
import hcmute.fit.event_management.entity.Mc;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IMcService {

    List<McDTO> getListMc();

    boolean addMc(MultipartFile image,McDTO mcDto);

    boolean updateMc(MultipartFile image, McDTO mcDto);

    boolean deleteMc(Integer mcId);

    McDTO findMcById(Integer mcId);
}
