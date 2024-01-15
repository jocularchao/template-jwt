<script setup>
//导入element的图标组件User、lock
import {Lock, User} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import {login} from "@/net/index.js";
import router from "@/router/index.js";

//保存表单的数据
const form = reactive(
    {
      username:'',
      password:'',
      remember:false
    }
)
const formRef = ref()

//校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名' }
  ],
  password: [
    { required: true, message: '请输入密码'}
  ]
}

//用户登录
function userLogin(){
  //表单验证
  formRef.value.validate((valid)=>{
    if (valid){ //验证有效
      login(
          form.username,
          form.password,
          form.remember,
          ()=>{
            router.push('/index')
          })
    }
  })
}

</script>

<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 150px">
      <div style="font-size: 25px">登录</div>
      <div style="font-size: 14px;color: darkgray">在进入系统前请先输入用户名和密码进行登录</div>
    </div>

    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" ref="formRef">
        <!--用户名-->
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名/邮箱">
            <!--使用element的用户图标-->
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <!--密码-->
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" maxlength="20" style="margin-top: 10px" placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <!--左右布局：记住我和忘记密码-->
        <el-row style="margin-top: 5px">
          <el-col :span="12" style="text-align: left">
            <el-form-item prop="remember">
              <el-checkbox label="记住我" v-model="form.remember" />
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link>忘记密码?</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!--登录按钮-->
    <div style="margin-top: 40px">
      <el-button @click="userLogin()" style="width: 270px;border-radius: 3px" type="success" plain >立即登录</el-button>
    </div>
    <!--提示注册-->
    <el-divider>
      <span style="color: gray;font-size: 13px">没有账户？</span>
    </el-divider>
    <!--注册按钮-->
    <el-button style="width: 270px;border-radius: 3px" type="warning" plain>注册账号</el-button>
  </div>
</template>

<style scoped>

</style>