package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.entity.Manager;
import hcmute.fit.event_management.repository.ManagerRepository;
import hcmute.fit.event_management.service.IManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManagerServiceImpl implements IManagerService {
    @Autowired
    private ManagerRepository managerRepository;

}
