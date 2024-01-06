<script setup>
//导入element的图标组件User、lock
import {Lock, User} from "@element-plus/icons-vue";

</script>

<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 150px">
      <div style="font-size: 25px">登录</div>
      <div style="font-size: 14px;color: darkgray">在进入系统前请先输入用户名和密码进行登录</div>
    </div>

    <div style="margin-top: 50px">
      <!--用户名-->
      <el-input type="text" placeholder="用户名/邮箱">
        <!--使用element的用户图标-->
        <template #prefix>
          <el-icon>
            <User/>
          </el-icon>
        </template>
      </el-input>
      <!--密码-->
      <el-input type="password" style="margin-top: 13px" placeholder="密码">
        <template #prefix>
          <el-icon>
            <Lock/>
          </el-icon>
        </template>
      </el-input>
    </div>
    <!--左右布局：记住我和忘记密码-->
    <el-row>
      <el-col :span="12" style="text-align: left">
        <el-checkbox label="记住我" v-model="checked1"/>
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-link>忘记密码?</el-link>
      </el-col>
    </el-row>
    <!--登录按钮-->
    <div style="margin-top: 25px;margin-top: 40px">
      <el-button style="width: 270px;border-radius: 3px" type="success">立即登录</el-button>
    </div>
    <!--提示注册-->
    <el-divider>
      <span style="color: gray;font-size: 13px">没有账户？</span>
    </el-divider>
    <!--注册按钮-->
    <el-button style="width: 270px;border-radius: 3px" type="warning">注册账号</el-button>
  </div>
</template>

<style scoped>

</style>