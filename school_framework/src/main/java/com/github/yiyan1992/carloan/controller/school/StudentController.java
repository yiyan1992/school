package com.github.yiyan1992.carloan.controller.school;

import com.github.yiyan1992.carloan.config.MagicValue;
import com.github.yiyan1992.carloan.entity.base.Response;
import com.github.yiyan1992.carloan.entity.response.StudentCourseResponse;
import com.github.yiyan1992.carloan.entity.school.SchoolStudent;
import com.github.yiyan1992.carloan.entity.sys.ShiroUser;
import com.github.yiyan1992.carloan.service.school.SchoolStudentService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private SchoolStudentService schoolStudentService;

    @RequiresRoles(MagicValue.LOGIN_TYPE_MANAGE)
    @PostMapping("/list")
    public Response list(SchoolStudent schoolStudent) {
        Page<SchoolStudent> list = schoolStudentService.findPageList(schoolStudent.getExample(), schoolStudent.getPageRequest());
        return Response.of(200, list);
    }

    @RequiresRoles(MagicValue.LOGIN_TYPE_STUDENT)
    @RequestMapping("/myCourse")
    public Response myCourse() {
        Subject subject = SecurityUtils.getSubject();
        ShiroUser user = (ShiroUser) subject.getPrincipals().getPrimaryPrincipal();
        if (!MagicValue.isStudent(user.getType())) {
            return Response.error("不是学生,无法使用");
        }
        List<StudentCourseResponse> list = schoolStudentService.findCourseBySchoolNo(user.getUsername());
        return Response.success(list);
    }

    @RequiresRoles(MagicValue.LOGIN_TYPE_MANAGE)
    @PostMapping("/findById/{id}")
    public Response findById(@PathVariable Integer id) {
        Optional<SchoolStudent> optional = schoolStudentService.findById(id);
        return Response.success(optional.get());
    }

    @RequiresRoles(MagicValue.LOGIN_TYPE_MANAGE)
    @PostMapping("/add")
    public Response add(SchoolStudent schoolStudent) {
        return Response.of(200, schoolStudentService.save(schoolStudent));
    }

    @RequiresRoles(MagicValue.LOGIN_TYPE_MANAGE)
    @PostMapping("/update")
    public Response update(SchoolStudent schoolYear) {
        return Response.of(200, schoolStudentService.save(schoolYear));
    }

    @RequiresRoles(MagicValue.LOGIN_TYPE_MANAGE)
    @PostMapping("/deleteById/{id}")
    public Response delete(@PathVariable Integer id) {
        schoolStudentService.deleteById(id);
        return Response.success("");
    }
}
