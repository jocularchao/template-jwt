package top.jocularchao.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import top.jocularchao.entity.BaseData;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author jocularchao
 * @date 2024-01-03 20:32
 * @description 用户类
 */
@Data
@TableName("db_account")
@AllArgsConstructor
public class Account implements BaseData {

    @TableId(type = IdType.AUTO)
    Integer id;
    String username;
    String password;
    String email;
    String role;
    Date registerTime;  //注册时间



}
