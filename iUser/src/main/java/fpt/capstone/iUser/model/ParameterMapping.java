package fpt.capstone.iUser.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "parameter_mapping")
public class ParameterMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "param")
    private String param;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "column_name")
    private String columnName;

    @Column(name = "description")
    private String description;
}
