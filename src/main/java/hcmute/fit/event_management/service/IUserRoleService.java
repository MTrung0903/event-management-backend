package hcmute.fit.event_management.service;

import hcmute.fit.event_management.entity.UserRole;
import hcmute.fit.event_management.entity.keys.AccountRoleId;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IUserRoleService {
    <S extends UserRole> List<S> findAll(Example<S> example);

    <S extends UserRole> List<S> findAll(Example<S> example, Sort sort);

    List<UserRole> findAll();

    List<UserRole> findAllById(Iterable<AccountRoleId> accountRoleIds);

    <S extends UserRole> S save(S entity);

    Optional<UserRole> findById(AccountRoleId accountRoleId);

    boolean existsById(AccountRoleId accountRoleId);

    long count();

    void deleteById(AccountRoleId accountRoleId);

    List<UserRole> findAll(Sort sort);

    Page<UserRole> findAll(Pageable pageable);

    <S extends UserRole> Optional<S> findOne(Example<S> example);

    <S extends UserRole> Page<S> findAll(Example<S> example, Pageable pageable);

    <S extends UserRole> long count(Example<S> example);

    <S extends UserRole> boolean exists(Example<S> example);

    <S extends UserRole, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
}
