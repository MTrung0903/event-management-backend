package hcmute.fit.event_management.service;

import hcmute.fit.event_management.dto.InviteDTO;
import hcmute.fit.event_management.entity.Invite;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IInviteService {
    List<InviteDTO> getListInvitesByEventId(int eventId);

    InviteDTO findInviteByNameOrEmail(String inviteInfor);

    InviteDTO findInvite(int inviteId);

    boolean addInvite(InviteDTO invite);

    boolean updateInvite(InviteDTO invite);

    boolean updateInviteStatus(Integer inviteId, String status);

    boolean deleteInvite(Integer inviteId);
}
