import {createRouter, createWebHistory} from "vue-router";

const router = createRouter({
    history:createWebHistory(import.meta.env.BASE_URL),
    routes:[
        {
            path:'/',       //路由地址
            name:'welcome', //路由名
            component:()=>import('@/views/WelcomeView.vue'),    //路由所引用的组件
            //子路由
            children:[
                //登录
                {
                    path:'',
                    name:'welcome-login',
                    component:()=>import('@/components/welcome/LoginPage.vue')
                },
                //注册
                {
                    path:'register',
                    name:'welcome-register',
                    component:()=>import('@/components/welcome/RegisterPage.vue')
                }
            ]
        },
        {
            path:'/index',
            name:'index',
            component:()=>import('@/views/IndexView.vue')
        }
    ]
})

//导出路由
export default router;