<script setup>

import {logout} from "@/net/index.js";
import router from "@/router/index.js";

function userLogout(){
  logout(()=>{
    router.push("/")
  })
}
</script>

<template>
  <div>welcome to template page</div>
  <!--登出按钮-->
  <div style="margin-top: 10px">
    <!--plain 为el-button的朴素样式-->
    <el-button type="danger" @click="userLogout()" plain>登出</el-button>
  </div>
</template>

<style scoped>

</style>