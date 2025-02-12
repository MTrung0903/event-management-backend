package hcmute.fit.event_management.service.Impl;

import hcmute.fit.event_management.entity.UserRole;
import hcmute.fit.event_management.entity.keys.AccountRoleId;
import hcmute.fit.event_management.repository.UserRoleRepository;
import hcmute.fit.event_management.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class UserRoleServiceImpl implements IUserRoleService {
    @Autowired
    UserRoleRepository userRoleRepository;

    @Override
    public <S extends UserRole> List<S> findAll(Example<S> example) {
        return userRoleRepository.findAll(example);
    }

    @Override
    public <S extends UserRole> List<S> findAll(Example<S> example, Sort sort) {
        return userRoleRepository.findAll(example, sort);
    }

    @Override
    public List<UserRole> findAll() {
        return userRoleRepository.findAll();
    }

    @Override
    public List<UserRole> findAllById(Iterable<AccountRoleId> accountRoleIds) {
        return userRoleRepository.findAllById(accountRoleIds);
    }

    @Override
    public <S extends UserRole> S save(S entity) {
        return userRoleRepository.save(entity);
    }

    @Override
    public Optional<UserRole> findById(AccountRoleId accountRoleId) {
        return userRoleRepository.findById(accountRoleId);
    }

    @Override
    public boolean existsById(AccountRoleId accountRoleId) {
        return userRoleRepository.existsById(accountRoleId);
    }

    @Override
    public long count() {
        return userRoleRepository.count();
    }

    @Override
    public void deleteById(AccountRoleId accountRoleId) {
        userRoleRepository.deleteById(accountRoleId);
    }

    @Override
    public List<UserRole> findAll(Sort sort) {
        return userRoleRepository.findAll(sort);
    }

    @Override
    public Page<UserRole> findAll(Pageable pageable) {
        return userRoleRepository.findAll(pageable);
    }

    @Override
    public <S extends UserRole> Optional<S> findOne(Example<S> example) {
        return userRoleRepository.findOne(example);
    }

    @Override
    public <S extends UserRole> Page<S> findAll(Example<S> example, Pageable pageable) {
        return userRoleRepository.findAll(example, pageable);
    }

    @Override
    public <S extends UserRole> long count(Example<S> example) {
        return userRoleRepository.count(example);
    }

    @Override
    public <S extends UserRole> boolean exists(Example<S> example) {
        return userRoleRepository.exists(example);
    }

    @Override
    public <S extends UserRole, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return userRoleRepository.findBy(example, queryFunction);
    }

    public <S extends UserRole> List<S> saveAll(Iterable<S> entities) {
        return userRoleRepository.saveAll(entities);
    }
}
