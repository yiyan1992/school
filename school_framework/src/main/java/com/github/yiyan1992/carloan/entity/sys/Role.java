package com.github.yiyan1992.carloan.entity.sys;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


/**
 * @author admin
 */
@Data
@Entity
@Table(name = "s_role")
public class Role implements Serializable {

    @Id
    @Column(name = "role_name",length = 50)
    private String roleName;

    @Column(name = "role_desc",length = 200)
    private String roleDesc;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "s_role_menu",
            joinColumns = {@JoinColumn(name = "role_name",foreignKey = @ForeignKey(name = "role_name"))},
            inverseJoinColumns = {@JoinColumn(name = "menu_id",foreignKey = @ForeignKey(name = "id"))}
            )
    private Set<Menu> menus;

}
