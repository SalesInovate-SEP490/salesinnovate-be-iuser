package fpt.capstone.iUser.repository;

import fpt.capstone.iUser.model.ParameterMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParameterMappingRepository extends JpaRepository<ParameterMapping, Long> {
    List<ParameterMapping> findByParamIn(List<String> params);
}
