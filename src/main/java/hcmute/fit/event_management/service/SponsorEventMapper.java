package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.SponsorEventDTO;
import hcmute.fit.event_management.entity.SponsorEvent;
import hcmute.fit.event_management.service.Impl.CloudinaryService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CloudinaryService.class)
public interface SponsorEventMapper {

    @Mapping(target = "sponsorId", source = "sponsorEvent.sponsor.sponsorId")
    @Mapping(target = "sponsorName", source = "sponsorEvent.sponsor.sponsorName")
    @Mapping(target = "sponsorEmail", source = "sponsorEvent.sponsor.sponsorEmail")
    @Mapping(target = "sponsorAddress", source = "sponsorEvent.sponsor.sponsorAddress")
    @Mapping(target = "sponsorLogo", source = "sponsorEvent.sponsor.sponsorLogo", qualifiedByName = "getFileUrl")
    @Mapping(target = "sponsorPhone", source = "sponsorEvent.sponsor.sponsorPhone")
    @Mapping(target = "sponsorWebsite", source = "sponsorEvent.sponsor.sponsorWebsite")
    @Mapping(target = "sponsorRepresentativeName", source = "sponsorEvent.sponsor.sponsorRepresentativeName")
    @Mapping(target = "sponsorRepresentativeEmail", source = "sponsorEvent.sponsor.sponsorRepresentativeEmail")
    @Mapping(target = "sponsorRepresentativePhone", source = "sponsorEvent.sponsor.sponsorRepresentativePhone")
    @Mapping(target = "sponsorRepresentativePosition", source = "sponsorEvent.sponsor.sponsorRepresentativePosition")
    @Mapping(target = "sponsorType", source = "sponsorEvent.sponsorType")
    @Mapping(target = "sponsorLevel", source = "sponsorEvent.sponsorLevel")
    @Mapping(target = "sponsorStartDate", source = "sponsorEvent.sponsorStartDate")
    @Mapping(target = "sponsorEndDate", source = "sponsorEvent.sponsorEndDate")
    @Mapping(target = "sponsorStatus", source = "sponsorEvent.sponsorStatus")
    SponsorEventDTO toSponsorEventDTO(SponsorEvent sponsorEvent);
}