package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.SponsorDTO;
import hcmute.fit.event_management.entity.Sponsor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ISponsorService {

    List<SponsorDTO> getAllSponsors();

    SponsorDTO getSponsorById(int id);

    boolean addSponsor(MultipartFile sponsorLogo, SponsorDTO sponsorDTO);

    boolean updateSponsor(MultipartFile sponsorLogo, SponsorDTO sponsorDTO);

    boolean updateSponsor(SponsorDTO sponsorDTO);

    boolean deleteSponsor(int id);

    boolean addSponsorForEvent(int sponsorId, int eventId);

    boolean addNewSponsorForEvent(int eventId, MultipartFile logo, SponsorDTO sponsorDTO);

    List<SponsorDTO> getAllSponsorByEventId(int eventId);

    List<SponsorDTO> getSponsorForAddNew(int eventId);

    boolean deleteSponsorEvent(int eventId, int sponsorId);
}
