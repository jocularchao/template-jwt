import { createApp } from 'vue'
import App from './App.vue'
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import router from './router/index.js'
import axios from "axios";

const app = createApp(App)

//使用element完整导入
app.use(ElementPlus)

//使用路由
app.use(router)

//连接后端服务器
axios.defaults.baseURL='http://localhost:8080'

app.mount('#app')