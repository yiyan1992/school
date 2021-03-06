package com.github.yiyan1992.carloan.entity.school;

import com.github.yiyan1992.carloan.entity.base.Request;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 教师
 */
@Getter
@Setter
@Entity
@Table(name = "school_teacher")
public class SchoolTeacher extends Request<SchoolTeacher> implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false, length = 80)
    private String name;

    /**
     * 工号
     */
    @Column(length = 20, nullable = false, unique = true)
    private String workNo;

    @Column(nullable = false, unique = true, length = 18)
    private String idCard;

    @Column(length = 50)
    private String password;


    @Override
    public Example<SchoolTeacher> getExample() {
        return Example.of(this,
                ExampleMatcher.matching()
                        .withMatcher("name", matcher -> matcher.contains())
                        .withMatcher("workNo", matcher -> matcher.contains())
                        .withMatcher("idCard", matcher -> matcher.contains())
        );
    }
}
