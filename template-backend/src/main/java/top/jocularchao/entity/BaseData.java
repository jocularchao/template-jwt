package top.jocularchao.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * @author jocularchao
 * @date 2024-01-04 14:12
 * @description
 */
public interface BaseData {

    default <V>V asViewObject(Class<V> clazz, Consumer<V> consumer){
        V v = this.asViewObject(clazz);
        consumer.accept(v);
        return v;
    }

    default <V>V asViewObject(Class<V> clazz){

        try {
            //把全部字段取出来
            Field[] declaredFields = clazz.getDeclaredFields();

            //获得构造器
            Constructor<V> constructor = clazz.getConstructor();
            //获取任意类型的vo对象
            V v = constructor.newInstance();
            for (Field field : declaredFields) convert(field,v);
            return v;


        }catch (ReflectiveOperationException exception){//有可能出现反射异常
            throw new RuntimeException(exception.getMessage());
        }


    }
    private void convert(Field field,Object vo){
        try {
            Field source = this.getClass().getDeclaredField(field.getName());

            field.setAccessible(true);
            source.setAccessible(true);

            field.set(vo,source.get(this));
        } catch (IllegalAccessException | NoSuchFieldException ignored) {}

    }


}
