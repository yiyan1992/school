<template>
  <div id="app">
    <el-row class="home-top">
      <el-col :span="5">课程管理系统</el-col>
      <el-col :span="15">&nbsp;</el-col>
      <el-col :span="4">
        <el-dropdown trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">
            {{userType}}{{name}}
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="pwd">密码</el-dropdown-item>
            <el-dropdown-item command="logout">注销</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
    </el-row>
    <el-row class="home-body">
      <el-col :span="4">
        <el-menu router default-active="1" menu-trigger="click" class="el-menu-vertical-demo">
          <el-submenu index="1" v-if="userType=='管理员:'">
            <template slot="title">
              <span>系统管理</span>
            </template>
            <el-menu-item index="/user">管理员管理</el-menu-item>
            <el-menu-item index="/role">管理员管理</el-menu-item>
            <el-menu-item index="/menu">管理员管理</el-menu-item>
          </el-submenu>
          <el-submenu index="2" v-if="userType=='管理员:'">
            <template slot="title">
              <span>学校管理</span>
            </template>
            <el-menu-item index="/year">学年管理</el-menu-item>
            <el-menu-item index="/course">课程管理</el-menu-item>
            <el-menu-item index="/class">班级管理</el-menu-item>
            <el-menu-item index="/teacher">教师管理</el-menu-item>
            <el-menu-item index="/student">学生管理</el-menu-item>
          </el-submenu>
          <el-submenu index="3" v-if="userType=='老师:'">
            <template slot="title">
              <span>教师信息</span>
            </template>
            <el-menu-item index="/teacherCourse">教师改分</el-menu-item>
          </el-submenu>
          <el-submenu index="4" v-if="userType=='学生:'">
            <template slot="title">
              <span>学生信息</span>
            </template>
            <el-menu-item index="/studentCourse">我的课程</el-menu-item>
          </el-submenu>
        </el-menu>
      </el-col>
      <el-col :span="20">
        <router-view></router-view>
      </el-col>
    </el-row>

    <el-dialog title="修改密码" :visible.sync="dialogFormVisible">
      <el-form :model="form" label-width="90px" :rules="rules" ref="form">
        <el-form-item label="旧密码" prop="password">
          <el-input v-model="form.password" autocomplete="off" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="password2">
          <el-input v-model="form.password2" autocomplete="off" show-password></el-input>
        </el-form-item>
        <el-form-item label="重复新密码" prop="password3">
          <el-input v-model="form.password3" autocomplete="off" show-password></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="updatePwd">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Vue from "vue";

export default Vue.extend({
  data() {
    let validatePass3 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (value !== this.form.password2) {
        callback(new Error("两次输入密码不一致!"));
      } else {
        callback();
      }
    };
    return {
      dialogFormVisible: false,
      name: "",
      userType: "",
      form: {
        password: "",
        password2: "",
        password3: ""
      },
      rules: {
        password: [
          { required: true, message: "请输入旧密码", trigger: "blur" }
        ],
        password2: [
          { required: true, message: "请输入新密码", trigger: "blur" }
        ],
        password3: [{ validator: validatePass3, trigger: "blur" }]
      }
    };
  },
  created() {
    let t = this;
    Vue.axios.post("/user/home").then(function(res) {
      if (res.data.code == 200) {
        let user = res.data.entity;
        t.name = user.name;
        t.userType = user.userType;
      }
    });
  },
  methods: {
    handleCommand(command) {
      switch (command) {
        case "pwd":
          this.dialogFormVisible = true;
          break;
        case "logout":
          this.logout();
          break;
      }
    },
    updatePwd() {
      let t = this;
      Vue.axios.post("/user/updatePwd", this.form).then(function(res) {
        if (res.data.code == 200) {
          t.$message({
            message: "密码修改成功!",
            type: "success",
            showClose: true
          });
        } else if (res.data.code == 500) {
          t.$message({
            message: "密码修改失败!",
            type: "error",
            showClose: true
          });
        }
      });
      this.dialogFormVisible = false;
    },
    logout() {
      sessionStorage.clear();
      this.$router.replace("/login");
    }
  }
});
</script>

<style>
#app {
  text-align: center;
}

.home-top {
  height: 20%;
  margin-bottom: 5px;
}

.home-body {
  height: 80%;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
}

.el-icon-arrow-down {
  font-size: 12px;
}
</style>
