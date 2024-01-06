<script setup>

</script>

<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <!--左半边-->
    <div style="flex: 1;background-color: antiquewhite">
      <el-image style="width: 100%;height: 100%;" fit="cover" src="src/assets/mao3.jpg"/>
    </div>
    <!--设置主题内容：绝对定位以便在图片上写内容 -->
    <div class="welcome-title">
      <div>模板</div>
      <p>学习登陆注册模板</p>
    </div>
    <!--右半边 固定宽度-->
    <div style="width: 300px;background-color: white">
      <router-view />
    </div>
  </div>
</template>

<style scoped>
.welcome-title {
  position: absolute;
  left: 40px;
  bottom: 45px;

}

.welcome-title div {
  color: white;
  font-size: 60px;
  font-weight: bold;
  text-shadow: 0 1px 8px blanchedalmond;
}
.welcome-title p {
  color: white;
  top: 10px;
}
</style>