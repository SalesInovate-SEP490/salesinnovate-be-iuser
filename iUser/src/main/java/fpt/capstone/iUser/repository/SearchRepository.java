package fpt.capstone.iUser.repository;


import fpt.capstone.iUser.model.AddressInformation;
import fpt.capstone.iUser.model.Role;
import fpt.capstone.iUser.model.Users;
import fpt.capstone.iUser.repository.specification.SpecSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static fpt.capstone.iUser.util.AppConst.*;


@Component
@Slf4j
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Page<Users> searchUserByCriteriaWithJoin(String adminId,List<SpecSearchCriteria> params, Pageable pageable) {
        List<Users> users = getAllLeadsWithJoin(adminId,params, pageable);
        Long totalElements = countAllLeadsWithJoin(adminId,params);
        return new PageImpl<>(users, pageable, totalElements);
    }

    private List<Users> getAllLeadsWithJoin(String adminId,List<SpecSearchCriteria> params, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Users> query = criteriaBuilder.createQuery(Users.class);
        Root<Users> leadsRoot = query.from(Users.class);

        List<Predicate> predicateList = new ArrayList<>();

        for (SpecSearchCriteria criteria : params) {
            String key = criteria.getKey();
            if (key.contains(ADDRESS_REGEX)) {
                Join<AddressInformation,Users> addressRoot = leadsRoot.join("addressInformation");
                predicateList.add(toJoinPredicate(addressRoot, criteriaBuilder, criteria, ADDRESS_REGEX));
            } else if (key.contains(ROLE_REGEX)) {
                Join<Users,Role> join = leadsRoot.join("roles", JoinType.INNER);
                predicateList.add(criteriaBuilder.equal(join.get("name"), criteria.getValue()));
            }
            else {
                if (key.equals("createDate")) {
                    predicateList.add(toPredicateDateTime(leadsRoot, criteriaBuilder, criteria));
                } else
                    predicateList.add(toPredicate(leadsRoot, criteriaBuilder, criteria));
            }
        }
        if(adminId==null){
            predicateList.add(criteriaBuilder.equal(leadsRoot.get("isActive"),true));
        }
        Predicate predicates = criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        query.where(predicates);

        return entityManager.createQuery(query)
                .setFirstResult(pageable.getPageNumber()*pageable.getPageSize())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    private Long countAllLeadsWithJoin(String adminId,List<SpecSearchCriteria> params) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Users> leadsRoot = query.from(Users.class);

        List<Predicate> predicateList = new ArrayList<>();

        for (SpecSearchCriteria criteria : params) {
            String key = criteria.getKey();
            if (key.contains(ADDRESS_REGEX)) {
                Join<AddressInformation,Users> addressRoot = leadsRoot.join("addressInformation");
                predicateList.add(toJoinPredicate(addressRoot, criteriaBuilder, criteria, ADDRESS_REGEX));
            } else if (key.contains(ROLE_REGEX)) {
                Join<Users,Role> join = leadsRoot.join("roles", JoinType.INNER);
                predicateList.add(criteriaBuilder.equal(join.get("name"), criteria.getValue()));
            }
            else {
                if (key.equals("createDate")) {
                    predicateList.add(toPredicateDateTime(leadsRoot, criteriaBuilder, criteria));
                } else
                    predicateList.add(toPredicate(leadsRoot, criteriaBuilder, criteria));
            }
        }
        if(adminId==null){
            predicateList.add(criteriaBuilder.equal(leadsRoot.get("isActive"),true));
        }
        Predicate predicates = criteriaBuilder.and(predicateList.toArray(new Predicate[0]));

        query.select(criteriaBuilder.count(leadsRoot));
        query.where(predicates);

        return entityManager.createQuery(query).getSingleResult();
    }

    private Predicate toPredicate(@NonNull Root<?> root, @NonNull CriteriaBuilder builder,@NonNull  SpecSearchCriteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()),  criteria.getValue().toString() );
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    private Predicate toPredicateDateTime(@NonNull Root<?> root, @NonNull CriteriaBuilder builder, @NonNull SpecSearchCriteria criteria) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), LocalDateTime.parse(criteria.getValue().toString(), formatter));
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), LocalDateTime.parse(criteria.getValue().toString(), formatter));
            case LIKE -> builder.like(root.get(criteria.getKey()),  criteria.getValue().toString() );
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    private Predicate toJoinPredicate(@NonNull Join<?,Users> root,@NonNull  CriteriaBuilder builder,@NonNull  SpecSearchCriteria criteria, String regex) {
        String key = criteria.getKey();
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(key.replace(regex, "")), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(key.replace(regex, "")), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(key.replace(regex, "")), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(key.replace(regex, "")), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(key.replace(regex, "")),   criteria.getValue().toString() );
            case STARTS_WITH -> builder.like(root.get(key.replace(regex, "")), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(key.replace(regex, "")), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(key.replace(regex, "")), "%" + criteria.getValue() + "%");
        };
    }

}
