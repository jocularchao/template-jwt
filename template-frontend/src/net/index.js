import axios from "axios";
import {ElMessage} from "element-plus";



const authItemName = "access_token";



//token

// token的取出操作
function takeAccessToken() {

    //获得的值就是后端给客户端的data
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName);
    // 如果没拿到数据，返回空
    if (!str)   return null;
    // 如果拿到了，解析json值重新赋值给我们的object，并做判断
    const authObj = JSON.parse(str);

    //
    if(new Date(authObj.expire)<= new Date()){
        //如果日期时间小于当前时间，即过期了，需要我们重新登录，来获取新的token
        deleteAccessToken();
        ElMessage.warning("登录状态已过期，请重新登录");
        return null;
    }

    return authObj.token;
}

//token的删除操作
function deleteAccessToken() {

    localStorage.removeItem(authItemName);

    sessionStorage.removeItem(authItemName);
}


// token的保存操作
// 为什么要保存,token有什么用?我们后续的请求都会用token去进行一个校验,它会写在这个请求头里面,存起来给服务端,服务端才可以解析,
// 所以说请求成功之后,登陆成功之后,收到这个请求头,我们需要把token存起来用
/*
    token       token
    remember    记住我
    expire      token的过期时间  下次我们再使用这个token的时候发现已经超过了token的过期时间,那这个token就不能用了,请求就无意义
*/
function storeAccessToken(token, remember, expire) {
    // 把token expire信息,封装成实体对象
    // 当然还有很多其他方式把token保存到本地...
    const authObj = {token: token, expire: expire}
    // local storage  一直存储  或   session storage     只针对对话
    // 到底用哪个?我们用remember来判断    如果我们勾选了 remember  就  local storage,如果不勾选  session storage
    // authObj转成字符串
    const str = JSON.stringify(authObj);
    if (remember)
        localStorage.setItem(authItemName,str);
    else
        sessionStorage.setItem(authItemName,str);

}



// 默认的失败的回调函数
/*
    message     失败的信息
*/
const defaultFailure = (message, code, url) => {
    // 控制台发出警告,并把一些信息打印出来
    console.warn(`请求地址: ${url}, 状态码: ${code}, 错误信息:${message}`)

    // 用element的接口: ElMessage来弹一个弹窗,打印失败信息
    ElMessage.warning(message)
}

//错误回调函数
const defaultError = (err) => {
    // 控制台发出错误,并把一些信息打印出来
    console.error(err)

    // 用element的接口: ElMessage来弹一个弹窗,打印错误信息
    ElMessage.warning('发生了错误,请联系管理员')
}


//内部使用

// 内部使用的post请求
/*
    url     地址
    data    数据
    header  请求头
    success 请求成功的回调函数
    failure 请求失败的回调函数
    error   请求错误的回调函数
*/
function internalPost(url,
                      data,
                      headers,
                      success,
                      failure,
                      error = defaultError) {
    // 我们用axios的原生post请求来尝试一下
    // 这里请求头header->headers放入配置里面
    // then就是我们去请求的数据,请求成功之后得到的一个处理,请求成功后,把data解出来({data})=>{}
    // 因为我们后端做了统一,返回的数据都是JSON的格式,并且都是通过返回我们的code来表示是否成功
    axios.post(url, data, {headers: headers}).then(({data}) => {
        // code为200 成功
        if (data.code === 200) {
            success(data.data)
        } else { //失败,调用失败的回调函数
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))      //错误!!
}


// 内部使用的get请求
function internalGet(url, headers, success, failure, error = defaultError) {
    axios.get(url, {headers: headers}).then(({data}) => {    //回调函数 调用的data是后端传来的data、message、code的集合
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }
    }).catch(err => error(err))
}



//内部使用的获取请求头
function accessHeader() {
    const token = takeAccessToken();
    return token?{
        'Authorization': `Bearer ${takeAccessToken()}`
    }:{}
}
//额外封装get、post供内外部使用
function get(url,success,failure=defaultFailure) {
    internalGet(url,accessHeader(),success,failure);
}

function post(url,data,success,failure=defaultFailure) {
    internalPost(url,data,accessHeader(),success,failure);
}




// 外部开放给其他人用的post

// login
function login(username, password, remember, success, failure = defaultFailure) {
    internalPost('/api/auth/login', {
        username: username,
        password: password
    }, {        //因为axios默认是以JSON的格式去提交数据,spring security只支持表单登录,所以我们只能让它以表单的形式去提交
        //我们需要把它的content-type改一下
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {//成功的回调
        //登陆成功后，我们要保存token值
        storeAccessToken(data.token,remember,data.expire);
        ElMessage.success(`登录成功,欢迎${data.username}进入我们系统`)
        success(data)
    },failure);

}

//logout
//我们退出登陆的时候要把jwt令牌也带上，这样系统就知道哪个用户登出了，所以要怎么带上呢？
//我们去写一个内部调用的函数  accessHeader  来获取请求头
function logout(success,failure=defaultFailure) {
    get('/api/auth/logout',()=>{
        deleteAccessToken();
        ElMessage.success('退出登录成功，欢迎再次使用')
        success()
    },failure)
}

//导出登录接口
export {login, logout, get, post};