import {createRouter, createWebHistory} from "vue-router";
import {unAuthorized} from "@/net/index.js";

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
                    component:()=>import('@/views/welcome/LoginPage.vue')
                },
                //注册
                {
                    path:'register',
                    name:'welcome-register',
                    component:()=>import('@/views/welcome/RegisterPage.vue')
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

//配置路由守卫
router.beforeEach((to, from, next)=>{
    const isUnauthorized = unAuthorized()
    if (to.name.startsWith('welcome-') && !isUnauthorized){ //已经登录了
        next('/index')
    }else if (to.fullPath.startsWith('/index') && isUnauthorized) {//未登录
        next('/')
    }else {
        next()
    }
} )

//导出路由
export default router;