import { createApp } from 'vue'
import App from './App.vue'
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'

const app = createApp(App)

//使用element完整导入
app.use(ElementPlus)

app.mount('#app')