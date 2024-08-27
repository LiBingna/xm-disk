<template>
  <div class="container">
    <div style="height: 60px;position: fixed;top: 0;display: flex;justify-content: center;padding-left: 10px">
      <img src="@/assets/imgs/logo.png" alt="" style="width: 50px"/>
      <span style="color: #409EFF; font-size: 30px;font-weight: bold;margin-left: 5px">个人网盘系统</span>
    </div>
    <div style="flex: 1;display: flex;justify-content: center">
      <img src="@/assets/imgs/bg.png" alt="" style="width: 100%">
    </div>
    <div style="flex: 1;display: flex;align-items: center;justify-content: center">
      <div
          style="width: 400px; padding:50px 30px; box-shadow: 0 0 10px rgba(0,0,0,0.3) ;background-color: white; border-radius: 5px;">
        <div style="position: absolute; background-color: white; top: 100px; z-index: 999" v-if="slideVerifyShow">
          <slide-verify :l="42"
                        :r="10"
                        :w="300"
                        :h="155"
                        :accuracy="5"
                        :imgs="imgs"
                        slider-text="向右滑动"
                        @success="onSuccess"
                        ref="slideVerifyRef"
          ></slide-verify>
        </div>
        <div style="text-align: center; font-size: 20px; margin-bottom: 20px; font-weight: bold; ;color: #333">
          欢迎登录
        </div>
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <el-input size="medium" prefix-icon="el-icon-user" placeholder="请输入账号"
                      v-model="form.username"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input size="medium" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password
                      v-model="form.password"></el-input>
          </el-form-item>
          <el-form-item prop="role">
            <el-select style="width: 100%" size="medium" v-model="form.role">
              <el-option value="ADMIN" label="管理员"></el-option>
              <el-option value="USER" label="用户"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button size="medium" style="width: 100%; background-color: #2a60c9; border-color: #333; color: white"
                       @click="login">登 录
            </el-button>
          </el-form-item>

          <div style="display: flex; align-items: center">
            <div style="flex: 1"></div>
            <div style="flex: 1; text-align: right">
              还没有账号？请 <a href="/register">注册</a>
            </div>
          </div>
        </el-form>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      form: {role: 'ADMIN'},
      rules: {
        username: [
          {required: true, message: '请输入账号', trigger: 'blur'},
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
        ]
      }
    }
  },
  created() {

  },
  methods: {
    login() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          // 验证通过
          this.$request.post('/login', this.form).then(res => {
            if (res.code === '200') {
              localStorage.setItem("xm-user", JSON.stringify(res.data))  // 存储用户数据
              this.$message.success('登录成功')
              setTimeout(() => {
                if (res.data.role === 'ADMIN') {
                  location.href = '/home'
                } else {
                  location.href = '/front/home?category=all'
                }
              })
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  background-size: 100%;
  display: flex;
  align-items: center;
  color: #666;
}

a {
  color: #2a60c9;
}
</style>