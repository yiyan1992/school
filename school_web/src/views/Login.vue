<template>
  <div id="app">
    <header>
      <h1>课程管理系统</h1>
      <p></p>
    </header>
    <br>
    <br>
    <el-form :model="form" :rules="rules" ref="form" label-width="80px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" show-password></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="loginType">
        <el-select v-model="form.loginType" placeholder="请选择">
          <el-option label="学生" value="2" key="2"/>
          <el-option label="教师" value="1" key="1"/>
          <el-option label="管理" value="0" key="0"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="login('form')">登陆</el-button>
        <el-button type="primary" @click="resetForm('form')">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script lang="ts">
import Vue from "vue";

export default Vue.extend({
  data() {
    return {
      form: {
        username: "",
        password: "",
        loginType: ""
      },
      rules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          { min: 3, max: 20, message: "长度在 3 到 20 个字符", trigger: "blur" }
        ],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }]
      }
    };
  },
  methods: {
    login(form: string): void {
      let t = this;
      this.$refs[form].validate((valid: boolean) => {
        if (valid) {
          Vue.axios
            .post("/login", {
              username: t.form.username,
              password: t.form.password,
              loginType: t.form.loginType,
            })
            .then(function(res) {
              if (res.data.code == 200) {
                t.$message({
                  message: "登陆成功!",
                  type: "success",
                  showClose: true
                });
                sessionStorage.setItem("Authorization", res.data.entity);
                t.$router.replace("/home");
              }
              else{
                t.$message({
                  message: "登陆失败!",
                  type: "error",
                  showClose: true
                });
              }
            });
        } else {
          return false;
        }
      });
    },
    resetForm(form: string): void {
      this.$refs[form].resetFields();
    }
  }
});
</script>

<style>
#app {
  text-align: center;
  margin: auto;
}

.el-form {
  width: 30%;
  margin-left: 35%;
  margin-right: 35%;
}
.el-select {
  width: 100%;
}
</style>
